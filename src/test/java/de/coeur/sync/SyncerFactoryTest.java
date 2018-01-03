package de.coeur.sync;

import de.coeur.sync.category.CategorySyncer;
import de.coeur.sync.product.ProductSyncer;
import org.junit.Test;

import static de.coeur.sync.CliRunner.SYNC_MODULE_OPTION_CATEGORY_SYNC;
import static de.coeur.sync.CliRunner.SYNC_MODULE_OPTION_PRODUCT_SYNC;
import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class SyncerFactoryTest {
    @Test
    public void getSyncer_WithNullOptionValue_ShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> SyncerFactory.getSyncer(null))
            .isExactlyInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Blank argument supplied to \"-s\" or \"--sync\" option! Please choose either"
                + " \"products\" or \"categories\".");
    }

    @Test
    public void getSyncer_WithEmptyOptionValue_ShouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> SyncerFactory.getSyncer(""))
            .isExactlyInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Blank argument supplied to \"-s\" or \"--sync\" option! Please choose either"
                + " \"products\" or \"categories\".");
    }

    @Test
    public void getSyncer_WithUnknownOptionValue_ShouldThrowIllegalArgumentException() {
        final String unknownOptionValue = "anyOption";
        assertThatThrownBy(() -> SyncerFactory.getSyncer(unknownOptionValue))
            .isExactlyInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(format("Unknown argument \"%s\" supplied to \"-s\" or \"--sync\" option! Please "
                + "choose either \"products\" or \"categories\".", unknownOptionValue));
    }

    @Test
    public void getSyncer_WithValidOptionValue_ShouldReturnCorrectSyncer() {
        assertThat(SyncerFactory.getSyncer(SYNC_MODULE_OPTION_CATEGORY_SYNC)).isExactlyInstanceOf(CategorySyncer.class);
        assertThat(SyncerFactory.getSyncer(SYNC_MODULE_OPTION_PRODUCT_SYNC)).isExactlyInstanceOf(ProductSyncer.class);
    }
}
