package com.example.gameapp.data.repository;

import com.example.gameapp.data.remotedatasource.ProductRemoteDataSource;
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
public final class ProductRepositoryImpl_Factory implements Factory<ProductRepositoryImpl> {
  private final Provider<ProductRemoteDataSource> dataSourceProvider;

  public ProductRepositoryImpl_Factory(Provider<ProductRemoteDataSource> dataSourceProvider) {
    this.dataSourceProvider = dataSourceProvider;
  }

  @Override
  public ProductRepositoryImpl get() {
    return newInstance(dataSourceProvider.get());
  }

  public static ProductRepositoryImpl_Factory create(
      Provider<ProductRemoteDataSource> dataSourceProvider) {
    return new ProductRepositoryImpl_Factory(dataSourceProvider);
  }

  public static ProductRepositoryImpl newInstance(ProductRemoteDataSource dataSource) {
    return new ProductRepositoryImpl(dataSource);
  }
}
