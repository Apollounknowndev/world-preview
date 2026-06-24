package caeruleusTait.world.preview.client.gui.widgets;

import net.minecraft.client.gui.components.EditBox;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class EditBoxes {
    private EditBoxes() {
    }

    /**
     * 26.1 removed EditBox.setFilter, so input is validated in the responder instead: when the new
     * value fails {@code valid}, the box is reverted to its last valid value and the real responder
     * is not called.
     */
    public static Consumer<String> filtered(EditBox box, Predicate<String> valid, Consumer<String> responder) {
        return new Consumer<>() {
            private String lastValid = box.getValue();

            @Override
            public void accept(String x) {
                if (!valid.test(x)) {
                    if (!box.getValue().equals(lastValid)) {
                        box.setValue(lastValid);
                    }
                    return;
                }
                lastValid = x;
                responder.accept(x);
            }
        };
    }
}
