package de.coeur.sync.services.impl;


import de.coeur.sync.services.CategoryService;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.client.SphereClient;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.commercetools.sync.categories.utils.CategoryReferenceReplacementUtils.buildCategoryQuery;
import static com.commercetools.sync.commons.utils.CtpQueryUtils.queryAll;

public class CategoryServiceImpl implements CategoryService {
    private final SphereClient sphereClient;


    public CategoryServiceImpl(@Nonnull final SphereClient sphereClient) {
        this.sphereClient = sphereClient;
    }

    @Override
    public List<Category> fetchAll() {
        return queryAll(sphereClient, buildCategoryQuery(), page -> page)
            .thenApply(this::flattenCategoryPages)
            .toCompletableFuture()
            .join();
    }

    private List<Category> flattenCategoryPages(@Nonnull final List<List<Category>> categoryPages) {
        return categoryPages.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }
}
