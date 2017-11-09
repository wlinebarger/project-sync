package de.coeur.sync;

import static de.coeur.sync.category.CategorySyncer.syncCategories;

public class Main {
    /**
     * Application entry point.
     *
     * @param args all args
     */
    public static void main(final String[] args) {
        syncCategories().toCompletableFuture().join();
    }
}
