package de.coeur.sync.services.impl;


import de.coeur.sync.services.CategoryService;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.categories.expansion.CategoryExpansionModel;
import io.sphere.sdk.categories.queries.CategoryQuery;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.expansion.ExpansionPath;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.commercetools.sync.commons.utils.CtpQueryUtils.queryAll;

public class CategoryServiceImpl implements CategoryService{
    private final SphereClient sphereClient;


    public CategoryServiceImpl(@Nonnull final SphereClient sphereClient) {
        this.sphereClient = sphereClient;
    }

    @Override
    public List<Category> fetchAll() {
        final CategoryQuery categoryQuery = CategoryQuery.of()
                .withExpansionPaths(ExpansionPath.of("custom.type"))
                .plusExpansionPaths(CategoryExpansionModel::parent);

        return queryAll(sphereClient, categoryQuery, page -> page)
                .toCompletableFuture()
                .join().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }
}
