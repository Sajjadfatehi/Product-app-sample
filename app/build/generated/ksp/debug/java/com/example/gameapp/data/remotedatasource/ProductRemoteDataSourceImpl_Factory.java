package com.example.gameapp.data.remotedatasource;

import com.example.gameapp.data.apiservice.ProductService;
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
public final class ProductRemoteDataSourceImpl_Factory implements Factory<ProductRemoteDataSourceImpl> {
  private final Provider<ProductService> productServiceProvider;

  public ProductRemoteDataSourceImpl_Factory(Provider<ProductService> productServiceProvider) {
    this.productServiceProvider = productServiceProvider;
  }

  @Override
  public ProductRemoteDataSourceImpl get() {
    return newInstance(productServiceProvider.get());
  }

  public static ProductRemoteDataSourceImpl_Factory create(
      Provider<ProductService> productServiceProvider) {
    return new ProductRemoteDataSourceImpl_Factory(productServiceProvider);
  }

  public static ProductRemoteDataSourceImpl newInstance(ProductService productService) {
    return new ProductRemoteDataSourceImpl(productService);
  }
}
