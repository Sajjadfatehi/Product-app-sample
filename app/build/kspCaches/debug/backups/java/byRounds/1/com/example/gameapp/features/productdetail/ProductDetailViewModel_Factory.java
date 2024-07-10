package com.example.gameapp.features.productdetail;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class ProductDetailViewModel_Factory implements Factory<ProductDetailViewModel> {
  @Override
  public ProductDetailViewModel get() {
    return newInstance();
  }

  public static ProductDetailViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ProductDetailViewModel newInstance() {
    return new ProductDetailViewModel();
  }

  private static final class InstanceHolder {
    private static final ProductDetailViewModel_Factory INSTANCE = new ProductDetailViewModel_Factory();
  }
}
