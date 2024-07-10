package com.example.gameapp.features.products;

import com.example.gameapp.domain.usecase.FetchProductsUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class ProductsViewModel_Factory implements Factory<ProductsViewModel> {
  private final Provider<FetchProductsUseCase> fetchProductsUseCaseProvider;

  public ProductsViewModel_Factory(Provider<FetchProductsUseCase> fetchProductsUseCaseProvider) {
    this.fetchProductsUseCaseProvider = fetchProductsUseCaseProvider;
  }

  @Override
  public ProductsViewModel get() {
    return newInstance(fetchProductsUseCaseProvider.get());
  }

  public static ProductsViewModel_Factory create(
      Provider<FetchProductsUseCase> fetchProductsUseCaseProvider) {
    return new ProductsViewModel_Factory(fetchProductsUseCaseProvider);
  }

  public static ProductsViewModel newInstance(FetchProductsUseCase fetchProductsUseCase) {
    return new ProductsViewModel(fetchProductsUseCase);
  }
}
