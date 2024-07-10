package com.example.gameapp.domain.usecase;

import com.example.gameapp.domain.repository.ProductRepository;
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
public final class FetchProductsUseCase_Factory implements Factory<FetchProductsUseCase> {
  private final Provider<ProductRepository> productRepositoryProvider;

  public FetchProductsUseCase_Factory(Provider<ProductRepository> productRepositoryProvider) {
    this.productRepositoryProvider = productRepositoryProvider;
  }

  @Override
  public FetchProductsUseCase get() {
    return newInstance(productRepositoryProvider.get());
  }

  public static FetchProductsUseCase_Factory create(
      Provider<ProductRepository> productRepositoryProvider) {
    return new FetchProductsUseCase_Factory(productRepositoryProvider);
  }

  public static FetchProductsUseCase newInstance(ProductRepository productRepository) {
    return new FetchProductsUseCase(productRepository);
  }
}
