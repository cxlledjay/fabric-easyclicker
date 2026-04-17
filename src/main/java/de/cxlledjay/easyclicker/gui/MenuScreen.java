package de.cxlledjay.easyclicker.gui;

import de.cxlledjay.easyclicker.EasyClicker;
import de.cxlledjay.easyclicker.clicker.Config;
import de.cxlledjay.easyclicker.clicker.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MenuScreen extends Screen {
    public MenuScreen() {
        super(Text.literal(EasyClicker.MOD_ID.toUpperCase() + " SETTINGS").formatted(Formatting.BOLD, Formatting.UNDERLINE, Formatting.AQUA)
        );
    }

    @Override
    protected void init() {
        drawAutoClickerConfigMenuScreen();
    }

    private void drawAutoClickerConfigMenuScreen() {

        // ==========================================
        // 1. clicks per second (cps) selector
        // ==========================================

        // load current config
        int currentCpsSetting = ConfigManager.getInstance().getSpeed();
        int maxCpsSetting = ConfigManager.getInstance().getMaxSpeed() - 1;

        // 1/3 label
        TextWidget cpsLabel = new TextWidget(Text.literal("Clicks per Second"), this.textRenderer);

        // 2/3 manual input for cps
        TextFieldWidget cpsInput = new TextFieldWidget(this.textRenderer, 0, 0, 45, 20, Text.empty());
        cpsInput.setText(String.valueOf(currentCpsSetting));

        // 3/3 slider
        double startingSliderValue = (currentCpsSetting - 1.0) / ((float) maxCpsSetting);
        SyncableSlider cpsSlider = new SyncableSlider(cpsInput, startingSliderValue);

        // Attach the listener
        cpsInput.setChangedListener(text -> {
            try {
                // display typed cps
                int typedCps = Integer.parseInt(text);
                typedCps = net.minecraft.util.math.MathHelper.clamp(typedCps, 1, maxCpsSetting);
                cpsSlider.updateFromText((typedCps - 1.0) / ((float) maxCpsSetting));

                // and safe value to config
                ConfigManager.getInstance().setSpeed(typedCps);
            } catch (NumberFormatException e) {}
        });

        // "flexbox" group to keep everything for the cps in one line
        DirectionalLayoutWidget cpsControls = DirectionalLayoutWidget.horizontal().spacing(5);
        cpsControls.getMainPositioner().alignVerticalCenter();
        cpsControls.add(cpsLabel);
        cpsControls.add(cpsSlider);
        cpsControls.add(cpsInput);


        // ==========================================
        // 2. mode button
        // ==========================================

        // cycle between enum states
        CyclingButtonWidget<Config.ClickMode> modeButton = CyclingButtonWidget.builder((Config.ClickMode mode) -> Text.literal(mode.name()))
                .values(Config.ClickMode.values())
                .initially(ConfigManager.getInstance().getClickMode())
                .build(0, 0, 240, 20, Text.literal("Clicking Mode"), (button, newValue) -> {

                    // cycle enum
                    ConfigManager.getInstance().setClickMode(newValue);

                    // hide gui
                    boolean isFast = (newValue == Config.ClickMode.INDIVIDUAL);

                    // Instantly hide or show the CPS row!
                    cpsLabel.visible = isFast;
                    cpsSlider.visible = isFast;
                    cpsInput.visible = isFast;
                });


        // ==========================================
        // 3. build whole menu
        // ==========================================

        // make "flexbox" for middle of the screen
        DirectionalLayoutWidget menu = DirectionalLayoutWidget.vertical().spacing(15);
        menu.getMainPositioner().alignHorizontalCenter();

        // add all menu items
        menu.add(modeButton);
        menu.add(cpsControls);


        // ==========================================
        // 4. visibility and positioning
        // ==========================================

        // show correct visibility
        boolean initialMode = (ConfigManager.getInstance().getClickMode() == Config.ClickMode.INDIVIDUAL);
        cpsLabel.visible = initialMode;
        cpsSlider.visible = initialMode;
        cpsInput.visible = initialMode;


        // calculate size
        menu.refreshPositions();

        // positioning

        // 1/3 title
        TextWidget titleWidget = new TextWidget(this.title, this.textRenderer);
        SimplePositioningWidget.setPos(titleWidget, 0, 0, this.width, this.height, 0.5f, 0.3f);
        this.addDrawableChild(titleWidget);

        // 2/3 menu
        SimplePositioningWidget.setPos(menu, 0, 0, this.width, this.height, 0.5f, 0.5f);
        menu.forEachChild(this::addDrawableChild);
        modeButton.setWidth(menu.getWidth());

        // 3/3 close button
        ButtonWidget closeButton = ButtonWidget.builder(Text.literal("close"), button -> this.close())
                .width(menu.getWidth()) // set to menu width
                .build();
        SimplePositioningWidget.setPos(closeButton, 0, 0, this.width, this.height, 0.5f, 0.95f);
        this.addDrawableChild(closeButton);
    }

    // custom close method for saving config upon closing menu
    @Override
    public void close() {

        // save config
        ConfigManager.save();

        // continue as normal
        super.close();
    }



    // Custom Slider Class (ft. Gemini pro)
    private class SyncableSlider extends SliderWidget {
        private final TextFieldWidget linkedInput;

        public SyncableSlider(TextFieldWidget linkedInput, double startingValue) {
            // 150 width, 20 height
            super(0, 0, 75, 20, Text.empty(), startingValue);
            this.linkedInput = linkedInput;
        }

        @Override
        protected void updateMessage() {
            // We don't draw text on the slider
        }

        @Override
        protected void applyValue() {
            // Slider -> Text Box
            int draggedCps = (int) Math.round(1.0 + (this.value * (((float) ConfigManager.getInstance().getMaxSpeed()) - 1.0f )));

            // Safety check: Only update the text box if the number actually changed.
            // This prevents an infinite loop!
            if (!linkedInput.getText().equals(String.valueOf(draggedCps))) {
                linkedInput.setText(String.valueOf(draggedCps));

                // and safe value to config
                ConfigManager.getInstance().setSpeed(draggedCps);
            }
        }

        // THE MAGIC METHOD: This bypasses the private access error!
        public void updateFromText(double newValue) {
            this.value = newValue; // We are allowed to do this because we are inside the slider class
        }
    }
}
