package com.example.gameapp.core

import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.Companion.PROTECTED
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KProperty1

/**
 * Abstract base class for implementing the Model-View-Intent (MVI) architecture in Android ViewModel.
 *
 * This class extends Android's [ViewModel] and provides a foundation for managing the state and handling actions
 * within a MVI architecture. It introduces functions to observe and react to state changes, actions, and effects.
 *
 * For more information, refer to [BaseViewModel2 Migration Guide](https://gitlab.partdp.ir/partPay/client/android/part-pay/-/wikis/BaseViewModel2%20Migration%20Guide).
 *
 * @param S The type of state managed by the ViewModel.
 * @param A The type of actions that trigger state transitions in the ViewModel.
 * @param E The type of effects representing one-time UI events from ViewModel to Screen.
 *
 * @see androidx.lifecycle.ViewModel
 */
abstract class BaseViewModel<S, A, E>(initialState: S) : ViewModel() {
    /**
     * Mutable state flow representing the current state in the ViewModel.
     * The state flow emits the latest state value to all its collectors, making it suitable for scenarios
     * where components need to observe and react to changes in the ViewModel's state.
     *
     * The initial state is set to initialState, and subsequent state changes can be updated by invoking
     * the [setState] within the ViewModel.
     *
     * @see kotlinx.coroutines.flow.MutableStateFlow
     */
    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    /**
     * Channel for sending effects ([E]) to be observed by components outside this ViewModel.
     * Using a [Channel] ensures that the `send` function is suspended until there is at least one collector,
     * making it suitable for scenarios where you want to handle effects in a non-blocking manner.
     *
     * Effects represent one-time UI events or side effects that occur as a result of actions processed by the ViewModel.
     * Observing components can collect and react to these effects to update the UI or trigger specific behaviors.
     *
     * @see kotlinx.coroutines.channels.Channel
     */
    private val _effect = Channel<E>(capacity = UNLIMITED)
    val effect = _effect.receiveAsFlow()

    /**
     * Submits an effect of type [E] to be observed by components outside this ViewModel.
     *
     * This function allows subclasses to send one-time UI events or side effects ([E]) through the associated [Channel].
     * The effect will be emitted to any active collectors, allowing observing components to react accordingly.
     *
     * @param effect The effect to be submitted for observation.
     *
     * @see kotlinx.coroutines.channels.Channel
     */
    protected fun sendEffect(effect: E) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    /**
     * Submits an action of type [A] for observation within the ViewModel.
     *
     * This function is a convenient way to delegate the submitted action to the abstract `onEachAction` function,
     * allowing subclasses to define specific behavior when the ViewModel receives actions of type [A].
     *
     * @param action The action to be submitted for observation.
     *
     * @see onEachAction
     */
    fun submitAction(action: A) = onEachAction(action)

    /**
     * Abstract function for handling actions of type [A] within the ViewModel.
     *
     * Subclasses should implement this function to define specific behavior when the ViewModel receives actions.
     * The function will be called for each submitted action, allowing the ViewModel to react accordingly, such as updating
     * its state, triggering side effects, or interacting with external components.
     *
     * @param action The action of type [A] received by the ViewModel.
     *
     * @see BaseViewModel
     */
    protected abstract fun onEachAction(action: A)

    /**
     * Sets the state of the ViewModel using the provided state transformation [reducer].
     *
     * This function is intended to be used by subclasses and allows them to directly manipulate the ViewModel's
     * state by applying the specified state transformation [reducer].
     *
     * Caution: Avoid performing time-consuming tasks within the block. Instead, focus on efficiently updating
     * the state using a transformation like this: `setState { copy(x = newValue) }`.
     *
     * @param reducer A lambda function that transforms the current state of type [S] to a new state of type [S].
     *                The lambda is an extension function on [S] and should define the state transformation.
     *
     * @see kotlinx.coroutines.flow.getAndUpdate
     */
    @VisibleForTesting(otherwise = PROTECTED)
    fun setState(reducer: S.() -> S) {
        _state.getAndUpdate(reducer)
    }

    /**
     * Retrieves the current state from the [state].
     *
     * This function provides synchronous access to the current state of the [state].
     * It directly returns the current value of the [state]'s `value` property.
     *
     * @return The current state of the [state].
     *
     * @throws IllegalStateException if the [state] is not yet initialized or has been closed.
     *
     * @see kotlinx.coroutines.flow.StateFlow
     */
    fun getCurrentState(): S = state.value

    /**
     * Executes a suspending function within the ViewModel's coroutine scope, updating the state based on the result.
     *
     * This function is designed to streamline the execution of suspending functions within a MVI (Model-View-Intent)
     * architecture. It sets the state to a loading state before invoking the provided suspending function. Upon
     * completion, it updates the state with the result, whether it's a success or a failure.
     *
     * @param dispatcher The [CoroutineDispatcher] on which the suspending function will be executed.
     *                   If null, the default dispatcher is used.
     *
     * @param retainValue A property reference to the value in the current state that should be retained during
     *                    loading and failure states. This can be used for scenarios like retaining the current
     *                    data when reloading or retrying an operation.
     *
     * @param reducer A lambda function that defines how the state should be updated based on the result of the
     *                suspending function. It takes the current state [S] and the [AsyncResult] of type [T] and
     *                returns a new state [S].
     *
     * @return A [Job] representing the execution of the provided suspending function.
     *
     * @see kotlinx.coroutines.CoroutineDispatcher
     * @see kotlinx.coroutines.CoroutineScope.launch
     * @see AsyncResult
     */
    protected fun <T : Any?> (suspend () -> T).execute(
        dispatcher: CoroutineDispatcher? = null,
        retainValue: KProperty1<S, AsyncResult<T>>? = null,
        reducer: S.(AsyncResult<T>) -> S
    ): Job {
        setState { reducer(Loading(value = retainValue?.get(this)?.invoke())) }

        return viewModelScope.launch(dispatcher ?: EmptyCoroutineContext) {
            try {
                val result = invoke()
                setState { reducer(Success(result)) }
            } catch (error: Throwable) {
                setState { reducer(Fail(error, value = retainValue?.get(this)?.invoke())) }
            }
        }
    }

    /**
     * Executes a suspending function within the ViewModel's coroutine scope, transforming and updating the state based on the result.
     *
     * This function is designed to streamline the execution of suspending functions within a MVI (Model-View-Intent)
     * architecture. It sets the state to a loading state before invoking the provided suspending function. Upon
     * completion, it transforms the result using the specified [transform] function and updates the state with the
     * transformed result, whether it's a success or a failure.
     *
     * @param dispatcher The [CoroutineDispatcher] on which the suspending function will be executed.
     *                   If null, the default dispatcher is used.
     *
     * @param retainValue A property reference to the value in the current state that should be retained during
     *                    loading and failure states. This can be used for scenarios like retaining the current
     *                    data when reloading or retrying an operation.
     *
     * @param transform A lambda function that transforms the result of the suspending function ([T]) to a new
     *                  result type ([R]).
     *
     * @param reducer A lambda function that defines how the state should be updated based on the result of the
     *                transformed function. It takes the current state [S] and the [AsyncResult] of type [R] and
     *                returns a new state [S].
     *
     * @return A [Job] representing the execution of the provided suspending function.
     *
     * @see kotlinx.coroutines.CoroutineDispatcher
     * @see kotlinx.coroutines.CoroutineScope.launch
     * @see AsyncResult
     */
    protected fun <T : Any?, R : Any?> (suspend () -> T).execute(
        dispatcher: CoroutineDispatcher? = null,
        retainValue: KProperty1<S, AsyncResult<R>>? = null,
        transform: T.() -> R,
        reducer: S.(AsyncResult<R>) -> S
    ): Job {
        setState { reducer(Loading(value = retainValue?.get(this)?.invoke())) }

        return viewModelScope.launch(dispatcher ?: EmptyCoroutineContext) {
            try {
                val result = invoke()
                setState { reducer(Success(transform(result))) }
            } catch (error: Throwable) {
                setState { reducer(Fail(error, value = retainValue?.get(this)?.invoke())) }
            }
        }
    }

    /**
     * Executes a suspending function within the ViewModel's coroutine scope, providing optional callbacks for success and failure.
     *
     * This function is designed for simple execution of suspending functions within a MVI (Model-View-Intent) architecture.
     * It allows you to specify optional callback functions for both success and failure scenarios, making it flexible for
     * various use cases.
     *
     * @param onSuccess A callback function to be invoked upon successful execution of the suspending function.
     *                  If null, success events are not handled.
     *                  The callback receives the result of the suspending function ([T]) as its parameter.
     *
     * @param onFailure A callback function to be invoked if the suspending function throws an exception.
     *                  If null, failure events are not handled.
     *                  The callback receives the thrown [Throwable] as its parameter.
     *
     * @param dispatcher The [CoroutineDispatcher] on which the suspending function will be executed.
     *                   If null, the default dispatcher is used.
     *
     * @return A [Job] representing the execution of the provided suspending function.
     *
     * @throws IllegalArgumentException if both [onSuccess] and [onFailure] are null.
     *
     * @see kotlinx.coroutines.CoroutineDispatcher
     * @see kotlinx.coroutines.CoroutineScope.launch
     */
    protected fun <T : Any?> (suspend () -> T).execute(
        onSuccess: (suspend (T) -> Unit)? = null,
        onFailure: (suspend (Throwable) -> Unit)? = null,
        dispatcher: CoroutineDispatcher? = null
    ): Job {
        check(onSuccess != null || onFailure != null) { "onSuccess and onFail both cannot be null" }

        return viewModelScope.launch(dispatcher ?: EmptyCoroutineContext) {
            try {
                val result = invoke()
                onSuccess?.invoke(result)
            } catch (error: Throwable) {
                onFailure?.invoke(error)
            }
        }
    }

    /**
     * Executes a [Flow] within the ViewModel's coroutine scope, updating the state based on emitted values or errors.
     *
     * This function is designed to streamline the execution of a [Flow] within a MVI (Model-View-Intent) architecture.
     * It sets the state to a loading state before collecting values from the [Flow]. Upon each emitted value, it updates
     * the state with a success state. If the [Flow] encounters an error, it updates the state with a failure state.
     *
     * @param dispatcher The [CoroutineDispatcher] on which the [Flow] will be collected.
     *                   If null, the default dispatcher is used.
     *
     * @param retainValue A property reference to the value in the current state that should be retained during
     *                    loading and failure states. This can be used for scenarios like retaining the current
     *                    data when reloading or retrying an operation.
     *
     * @param reducer A lambda function that defines how the state should be updated based on the emitted values or errors.
     *                It takes the current state [S] and the [AsyncResult] of type [T] and returns a new state [S].
     *
     * @return A [Job] representing the execution of the provided [Flow].
     *
     * @see kotlinx.coroutines.CoroutineDispatcher
     * @see kotlinx.coroutines.flow.Flow
     * @see AsyncResult
     */
    protected fun <T> Flow<T>.execute(
        dispatcher: CoroutineDispatcher? = null,
        retainValue: KProperty1<S, AsyncResult<T>>? = null,
        reducer: S.(AsyncResult<T>) -> S,
    ): Job {
        setState { reducer(Loading(value = retainValue?.get(this)?.invoke())) }

        return catch { error ->
            setState { reducer(Fail(error, value = retainValue?.get(this)?.invoke())) }
        }
            .onEach { value -> setState { reducer(Success(value)) } }
            .launchIn(viewModelScope + (dispatcher ?: EmptyCoroutineContext))
    }

    /**
     * Observes the state in the ViewModel's coroutine scope and executes the provided action for each emitted state.
     * Supports cooperative cancellation, meaning the previous action will be cancelled if it has not completed before the
     * next one is emitted.
     *
     * This function is designed for observing the state within a MVI (Model-View-Intent) architecture.
     * It collects the state using the [state] channel and executes the provided [action] for each emitted state.
     *
     * @param action A suspending lambda function that defines the action to be executed for each emitted state.
     *               It takes the emitted state of type [S] as its parameter.
     *
     * @return A [Job] representing the observation of the state and the execution of the provided action.
     *
     * @see kotlinx.coroutines.CoroutineScope.launch
     * @see kotlinx.coroutines.flow.collectLatest
     */
    protected fun onEachState(
        action: suspend (S) -> Unit,
    ): Job {
        return viewModelScope.launch {
            state.collectLatest(action)
        }
    }

    /**
     * Observes a specific property of the state in the ViewModel's coroutine scope and executes the provided action
     * for each emitted value of that property. Supports cooperative cancellation, meaning the previous action will be
     * cancelled if it has not completed before the next one is emitted.
     *
     * Caution: The action block will be executed at least once due to the initial state setting. Keep this in mind when
     * designing the logic inside the action block.
     *
     * This function is designed for observing a specific property of the state within a MVI (Model-View-Intent) architecture.
     * It extracts the specified property using the [prop1] property reference, filters out consecutive duplicate values,
     * and executes the provided [action] for each emitted value of the property.
     *
     * @param prop1 A property reference ([KProperty1]) pointing to the property of the state [S] whose changes should
     *              trigger the provided action.
     *
     * @param action A suspending lambda function that defines the action to be executed for each emitted value of the specified property.
     *               It takes the emitted value of type [A] as its parameter.
     *
     * @return A [Job] representing the observation of the specified property and the execution of the provided action.
     *
     * @see kotlinx.coroutines.CoroutineScope.launch
     * @see kotlinx.coroutines.flow.map
     * @see kotlinx.coroutines.flow.distinctUntilChanged
     * @see kotlinx.coroutines.flow.collectLatest
     */
    protected fun <A> onEachState(
        prop1: KProperty1<S, A>,
        action: suspend (A) -> Unit,
    ): Job {
        return viewModelScope.launch {
            state
                .map { prop1.get(it) }
                .distinctUntilChanged()
                .collectLatest(action)
        }
    }

    /**
     * Observes specific properties of the state in the ViewModel's coroutine scope and executes the provided action
     * for each emitted combination of values from those properties. Supports cooperative cancellation, meaning the
     * previous action will be cancelled if it has not completed before the next one is emitted.
     *
     * Caution: The action block will be executed at least once due to the initial state setting. Keep this in mind when
     * designing the logic inside the action block.
     *
     * This function is designed for observing specific properties of the state within a MVI (Model-View-Intent) architecture.
     * It extracts the specified properties using the provided [prop1] and [prop2] property references, creates pairs of
     * their values, filters out consecutive duplicate pairs, and executes the provided [action] for each emitted pair
     * of property values.
     *
     * @param prop1 A property reference ([KProperty1]) pointing to the first property of the state [S] whose changes should
     *              trigger the provided action.
     *
     * @param prop2 A property reference ([KProperty1]) pointing to the second property of the state [S] whose changes should
     *              trigger the provided action.
     *
     * @param action A suspending lambda function that defines the action to be executed for each emitted combination of
     *               values from the specified properties. It takes the emitted values of type [A] and [B] as its parameters.
     *
     * @return A [Job] representing the observation of the specified properties and the execution of the provided action.
     *
     * @see kotlinx.coroutines.CoroutineScope.launch
     * @see kotlinx.coroutines.flow.map
     * @see kotlinx.coroutines.flow.distinctUntilChanged
     * @see kotlinx.coroutines.flow.collectLatest
     */
    protected fun <A, B> onEachState(
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        action: suspend (A, B) -> Unit,
    ): Job {
        return viewModelScope.launch {
            state
                .map { Pair(prop1.get(it), prop2.get(it)) }
                .distinctUntilChanged()
                .collectLatest { (a, b) -> action(a, b) }
        }
    }

    /**
     * Observes specific properties of the state in the ViewModel's coroutine scope and executes the provided action
     * for each emitted combination of values from those properties. Supports cooperative cancellation, meaning the
     * previous action will be cancelled if it has not completed before the next one is emitted.
     *
     * Caution: The action block will be executed at least once due to the initial state setting. Keep this in mind when
     * designing the logic inside the action block.
     *
     * This function is designed for observing specific properties of the state within a MVI (Model-View-Intent) architecture.
     * It extracts the specified properties using the provided [prop1], [prop2], and [prop3] property references, creates triples of
     * their values, filters out consecutive duplicate triples, and executes the provided [action] for each emitted triple
     * of property values.
     *
     * @param prop1 A property reference ([KProperty1]) pointing to the first property of the state [S] whose changes should
     *              trigger the provided action.
     *
     * @param prop2 A property reference ([KProperty1]) pointing to the second property of the state [S] whose changes should
     *              trigger the provided action.
     *
     * @param prop3 A property reference ([KProperty1]) pointing to the third property of the state [S] whose changes should
     *              trigger the provided action.
     *
     * @param action A suspending lambda function that defines the action to be executed for each emitted combination of
     *               values from the specified properties. It takes the emitted values of type [A], [B], and [C] as its parameters.
     *
     * @return A [Job] representing the observation of the specified properties and the execution of the provided action.
     *
     * @see kotlinx.coroutines.CoroutineScope.launch
     * @see kotlinx.coroutines.flow.map
     * @see kotlinx.coroutines.flow.distinctUntilChanged
     * @see kotlinx.coroutines.flow.collectLatest
     */
    protected fun <A, B, C> onEachState(
        prop1: KProperty1<S, A>,
        prop2: KProperty1<S, B>,
        prop3: KProperty1<S, C>,
        action: suspend (A, B, C) -> Unit,
    ): Job {
        return viewModelScope.launch {
            state
                .map { Triple(prop1.get(it), prop2.get(it), prop3.get(it)) }
                .distinctUntilChanged()
                .collectLatest { (a, b, c) ->
                    action(a, b, c)
                }
        }
    }

    /**
     * Observes an [AsyncResult] property of the state in the ViewModel's coroutine scope and executes the provided actions
     * for success and failure states. Supports cooperative cancellation, meaning the previous action will be cancelled
     * if it has not completed before the next one is emitted.
     *
     * This function is designed for observing an [AsyncResult] property of the state within a MVI (Model-View-Intent) architecture.
     * It extracts the specified [asyncProp] property using the provided [KProperty1], filters out consecutive duplicate values,
     * and executes the provided [onSuccess] or [onFail] action based on the state of the [AsyncResult].
     *
     * @param asyncProp A property reference ([KProperty1]) pointing to the [AsyncResult] property of the state [S] whose changes should
     *                  trigger the provided actions.
     *
     * @param onFail A suspending lambda function to be invoked if the [AsyncResult] enters a failure state.
     *               If null, failure events are not handled.
     *               The callback receives the thrown [Throwable] as its parameter.
     *
     * @param onSuccess A suspending lambda function to be invoked upon successful completion of the [AsyncResult].
     *                  If null, success events are not handled.
     *                  The callback receives the result of type [T] as its parameter.
     *
     * @return A [Job] representing the observation of the specified [AsyncResult] property and the execution of the provided actions.
     *
     * @throws IllegalArgumentException if both [onSuccess] and [onFail] are null.
     *
     * @see kotlinx.coroutines.CoroutineScope.launch
     * @see kotlinx.coroutines.flow.map
     * @see kotlinx.coroutines.flow.distinctUntilChanged
     * @see AsyncResult
     */
    protected fun <T> onAsyncResult(
        asyncProp: KProperty1<S, AsyncResult<T>>,
        onFail: (suspend (Throwable) -> Unit)? = null,
        onSuccess: (suspend (T) -> Unit)? = null,
    ): Job {
        check(onSuccess != null || onFail != null) { "onSuccess and onFail both cannot be null" }

        return viewModelScope.launch {
            state
                .map { asyncProp.get(it) }
                .distinctUntilChanged()
                .collectLatest { asyncValue ->
                    if (onSuccess != null && asyncValue is Success) {
                        onSuccess(asyncValue())
                    } else if (onFail != null && asyncValue is Fail) {
                        onFail(asyncValue.error)
                    }
                }
        }
    }
}
