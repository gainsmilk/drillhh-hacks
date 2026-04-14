package dev.gainsmilk.keepsprint.gui;

import dev.gainsmilk.keepsprint.KeepSprintMod;
import dev.gainsmilk.keepsprint.config.Config;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

/**
 * In-game config screen. Opened by RIGHT_ARROW.
 * Dark panel aesthetic with burgundy accent, module cards, custom toggle switches.
 */
public class TrenboloneGui extends GuiScreen {

    // --- Palette ---
    // ARGB values (AA RR GG BB)
    private static final int COL_BG_FADE        = 0xB0000000;
    private static final int COL_PANEL          = 0xF0141414;
    private static final int COL_PANEL_BORDER   = 0xFF2A2A2A;
    private static final int COL_HEADER_LINE    = 0xFF8A1C2E;
    private static final int COL_TITLE          = 0xFFF2E8EA;
    private static final int COL_CARD_BG        = 0xFF1C1C1C;
    private static final int COL_CARD_BG_HOVER  = 0xFF242424;
    private static final int COL_CARD_BORDER    = 0xFF2E2E2E;
    private static final int COL_MODULE_NAME    = 0xFFEDEDED;
    private static final int COL_MODULE_DESC    = 0xFF858585;
    private static final int COL_TOGGLE_OFF     = 0xFF333333;
    private static final int COL_TOGGLE_ON      = 0xFF8A1C2E;
    private static final int COL_TOGGLE_KNOB    = 0xFFF0F0F0;
    private static final int COL_SLIDER_TRACK   = 0xFF262626;
    private static final int COL_SLIDER_FILL    = 0xFF8A1C2E;
    private static final int COL_SLIDER_KNOB    = 0xFFF0F0F0;
    private static final int COL_BTN_BG         = 0xFF1F1F1F;
    private static final int COL_BTN_BG_HOVER   = 0xFF2A2A2A;
    private static final int COL_BTN_BORDER     = 0xFF3A3A3A;
    private static final int COL_BTN_TEXT       = 0xFFE8D5D9;
    private static final int COL_BTN_PRIMARY    = 0xFF8A1C2E;
    private static final int COL_BTN_PRIMARY_HOVER = 0xFFA82236;

    // --- State ---
    // Working copy, committed on Save
    private boolean keepSprintEnabled;
    private boolean omniSprintEnabled;
    private double  omniSprintMultiplier;
    private boolean fastPlaceEnabled;
    private boolean blockReachEnabled;
    private double  blockReachDistance;
    private boolean timerEnabled;
    private double  timerMultiplier;

    // --- Slider Bounds ---
    // Delegated to SliderMath for testability; aliased here for readability.
    private static final double OMNI_MIN  = SliderMath.OMNI_MIN,  OMNI_MAX  = SliderMath.OMNI_MAX;
    private static final double REACH_MIN = SliderMath.REACH_MIN, REACH_MAX = SliderMath.REACH_MAX;
    private static final double TIMER_MIN = SliderMath.TIMER_MIN, TIMER_MAX = SliderMath.TIMER_MAX;

    // --- Layout ---
    private int panelX, panelY, panelW, panelH;
    private int cardX, cardW;
    private int cardH = 30;
    private int cardGap = 4;
    private int sliderRowH = 20;
    private int sliderW = 140, sliderH = 4;
    private int toggleW = 28, toggleH = 14;

    // Module cards: 0=ks, 1=omni, 2=fp, 3=reach, 4=timer
    private int[] cardYs = new int[5];
    private int omniSliderX, omniSliderY;
    private int reachSliderX, reachSliderY;
    private int timerSliderX, timerSliderY;
    private int saveBtnX, saveBtnY, cancelBtnX, cancelBtnY, btnW = 100, btnH = 24;

    // Drag state
    private int draggingSlider = 0; // 0 none, 1 omni, 2 reach, 3 timer

    // --- Init ---

    public TrenboloneGui() {
        Config cfg = KeepSprintMod.CONFIG;
        this.keepSprintEnabled    = cfg.keepSprintEnabled;
        this.omniSprintEnabled    = cfg.omniSprintEnabled;
        this.omniSprintMultiplier = clampOmni(cfg.omniSprintMultiplier);
        this.fastPlaceEnabled     = cfg.fastPlaceEnabled;
        this.blockReachEnabled    = cfg.blockReachEnabled;
        this.blockReachDistance   = clampReach(cfg.blockReachDistance);
        this.timerEnabled         = cfg.timerEnabled;
        this.timerMultiplier      = clampTimer(cfg.timerMultiplier);
    }

    @Override
    public void initGui() {
        panelW = 360;
        panelH = 440;
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;

        cardW = panelW - 40;
        cardX = panelX + 20;

        int cursorY = panelY + 68;

        cardYs[0] = cursorY; cursorY += cardH + cardGap;                // KeepSprint
        cardYs[1] = cursorY; cursorY += cardH + cardGap;                // OmniSprint
        omniSliderX  = cardX + 16; omniSliderY  = cursorY + 6; cursorY += sliderRowH;
        cardYs[2] = cursorY; cursorY += cardH + cardGap;                // FastPlace
        cardYs[3] = cursorY; cursorY += cardH + cardGap;                // BlockReach
        reachSliderX = cardX + 16; reachSliderY = cursorY + 6; cursorY += sliderRowH;
        cardYs[4] = cursorY; cursorY += cardH + cardGap;                // Timer
        timerSliderX = cardX + 16; timerSliderY = cursorY + 6;

        int bottomY = panelY + panelH - btnH - 14;
        int gap = 12;
        int totalBtnW = btnW * 2 + gap;
        saveBtnX   = panelX + (panelW - totalBtnW) / 2;
        saveBtnY   = bottomY;
        cancelBtnX = saveBtnX + btnW + gap;
        cancelBtnY = bottomY;
    }

    // --- Drawing ---

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, COL_BG_FADE);
        drawPanel(panelX, panelY, panelX + panelW, panelY + panelH, COL_PANEL, COL_PANEL_BORDER);

        FontRenderer fr = mc.fontRendererObj;
        drawScaledString(fr, "TRENBOLONE BRIDGONATE", panelX + panelW / 2, panelY + 18, COL_TITLE, 1.6f, true);
        drawRect(panelX + 24, panelY + 50, panelX + panelW - 24, panelY + 51, COL_HEADER_LINE);

        drawModuleCard(mouseX, mouseY, cardYs[0], "KeepSprint", "preserve sprint through direction changes", keepSprintEnabled);
        drawModuleCard(mouseX, mouseY, cardYs[1], "OmniSprint", "velocity boost on strafe + backwards", omniSprintEnabled);
        drawSliderRow(fr, omniSliderX, omniSliderY,
                String.format("multiplier %.2fx", omniSprintMultiplier),
                omniSprintMultiplier, OMNI_MIN, OMNI_MAX, omniSprintEnabled);

        drawModuleCard(mouseX, mouseY, cardYs[2], "FastPlace", "1 block per tick holding RMB", fastPlaceEnabled);
        drawModuleCard(mouseX, mouseY, cardYs[3], "BlockReach", "extend interaction distance", blockReachEnabled);
        drawSliderRow(fr, reachSliderX, reachSliderY,
                String.format("distance %.1f blocks", blockReachDistance),
                blockReachDistance, REACH_MIN, REACH_MAX, blockReachEnabled);

        drawModuleCard(mouseX, mouseY, cardYs[4], "Timer", "client tickrate multiplier", timerEnabled);
        drawSliderRow(fr, timerSliderX, timerSliderY,
                String.format("speed %.2fx", timerMultiplier),
                timerMultiplier, TIMER_MIN, TIMER_MAX, timerEnabled);

        drawFlatButton(mouseX, mouseY, saveBtnX, saveBtnY, btnW, btnH, "SAVE", true);
        drawFlatButton(mouseX, mouseY, cancelBtnX, cancelBtnY, btnW, btnH, "CANCEL", false);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawModuleCard(int mouseX, int mouseY, int y, String name, String desc, boolean enabled) {
        boolean hover = inside(mouseX, mouseY, cardX, y, cardW, cardH);
        int bg = hover ? COL_CARD_BG_HOVER : COL_CARD_BG;
        drawPanel(cardX, y, cardX + cardW, y + cardH, bg, COL_CARD_BORDER);

        FontRenderer fr = mc.fontRendererObj;
        fr.drawStringWithShadow(name, cardX + 12, y + 6, COL_MODULE_NAME);
        fr.drawString(desc, cardX + 12, y + 17, COL_MODULE_DESC, false);

        int tX = cardX + cardW - toggleW - 12;
        int tY = y + (cardH - toggleH) / 2;
        drawToggle(tX, tY, enabled);
    }

    private void drawToggle(int x, int y, boolean on) {
        int trackCol = on ? COL_TOGGLE_ON : COL_TOGGLE_OFF;
        drawRect(x, y, x + toggleW, y + toggleH, trackCol);
        drawRect(x, y, x + toggleW, y + 1, 0x33000000);
        int knobSize = toggleH - 4;
        int knobX = on ? (x + toggleW - knobSize - 2) : (x + 2);
        int knobY = y + 2;
        drawRect(knobX, knobY, knobX + knobSize, knobY + knobSize, COL_TOGGLE_KNOB);
    }

    private void drawSliderRow(FontRenderer fr, int x, int y, String label, double value, double min, double max, boolean enabled) {
        int labelCol = enabled ? COL_MODULE_DESC : 0xFF555555;
        fr.drawString(label, x, y - 9, labelCol, false);

        drawRect(x, y, x + sliderW, y + sliderH, COL_SLIDER_TRACK);
        double frac = (value - min) / (max - min);
        if (frac < 0) frac = 0;
        if (frac > 1) frac = 1;
        int fillW = (int) Math.round(frac * sliderW);
        int fillCol = enabled ? COL_SLIDER_FILL : 0xFF4A4A4A;
        drawRect(x, y, x + fillW, y + sliderH, fillCol);
        int knobSize = 8;
        int knobX = x + fillW - knobSize / 2;
        int knobY = y - 2;
        int knobCol = enabled ? COL_SLIDER_KNOB : 0xFF888888;
        drawRect(knobX, knobY, knobX + knobSize, knobY + knobSize, knobCol);
    }

    private void drawFlatButton(int mouseX, int mouseY, int x, int y, int w, int h, String text, boolean primary) {
        boolean hover = inside(mouseX, mouseY, x, y, w, h);
        int bg = primary
                ? (hover ? COL_BTN_PRIMARY_HOVER : COL_BTN_PRIMARY)
                : (hover ? COL_BTN_BG_HOVER : COL_BTN_BG);
        drawPanel(x, y, x + w, y + h, bg, COL_BTN_BORDER);
        FontRenderer fr = mc.fontRendererObj;
        int tw = fr.getStringWidth(text);
        int color = primary ? 0xFFFFFFFF : COL_BTN_TEXT;
        fr.drawStringWithShadow(text, x + (w - tw) / 2f, y + (h - 8) / 2f, color);
    }

    private void drawPanel(int x1, int y1, int x2, int y2, int bg, int border) {
        drawRect(x1, y1, x2, y2, bg);
        drawRect(x1, y1, x2, y1 + 1, border);
        drawRect(x1, y2 - 1, x2, y2, border);
        drawRect(x1, y1, x1 + 1, y2, border);
        drawRect(x2 - 1, y1, x2, y2, border);
    }

    private void drawScaledString(FontRenderer fr, String text, int x, int y, int color, float scale, boolean shadow) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1f);
        int w = fr.getStringWidth(text);
        if (shadow) fr.drawStringWithShadow(text, -w / 2f, 0, color);
        else fr.drawString(text, -w / 2, 0, color, false);
        GlStateManager.popMatrix();
    }

    private boolean inside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    // --- Input ---

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            for (int i = 0; i < cardYs.length; i++) {
                if (inside(mouseX, mouseY, cardX, cardYs[i], cardW, cardH)) {
                    toggleModule(i);
                    return;
                }
            }
            if (inside(mouseX, mouseY, omniSliderX, omniSliderY - 4, sliderW, sliderH + 12)) {
                draggingSlider = 1;
                omniSprintMultiplier = clampOmni(fractionToValue(mouseX, omniSliderX, OMNI_MIN, OMNI_MAX));
                return;
            }
            if (inside(mouseX, mouseY, reachSliderX, reachSliderY - 4, sliderW, sliderH + 12)) {
                draggingSlider = 2;
                blockReachDistance = clampReach(fractionToValue(mouseX, reachSliderX, REACH_MIN, REACH_MAX));
                return;
            }
            if (inside(mouseX, mouseY, timerSliderX, timerSliderY - 4, sliderW, sliderH + 12)) {
                draggingSlider = 3;
                timerMultiplier = clampTimer(fractionToValue(mouseX, timerSliderX, TIMER_MIN, TIMER_MAX));
                return;
            }
            if (inside(mouseX, mouseY, saveBtnX, saveBtnY, btnW, btnH)) { saveAndClose(); return; }
            if (inside(mouseX, mouseY, cancelBtnX, cancelBtnY, btnW, btnH)) { mc.displayGuiScreen(null); return; }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (clickedMouseButton == 0) {
            if (draggingSlider == 1) {
                omniSprintMultiplier = clampOmni(fractionToValue(mouseX, omniSliderX, OMNI_MIN, OMNI_MAX));
                return;
            }
            if (draggingSlider == 2) {
                blockReachDistance = clampReach(fractionToValue(mouseX, reachSliderX, REACH_MIN, REACH_MAX));
                return;
            }
            if (draggingSlider == 3) {
                timerMultiplier = clampTimer(fractionToValue(mouseX, timerSliderX, TIMER_MIN, TIMER_MAX));
                return;
            }
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) draggingSlider = 0;
        super.mouseReleased(mouseX, mouseY, state);
    }

    // --- State Commit ---

    private void toggleModule(int i) {
        switch (i) {
            case 0: keepSprintEnabled = !keepSprintEnabled; break;
            case 1: omniSprintEnabled = !omniSprintEnabled; break;
            case 2: fastPlaceEnabled  = !fastPlaceEnabled; break;
            case 3: blockReachEnabled = !blockReachEnabled; break;
            case 4: timerEnabled      = !timerEnabled; break;
        }
    }

    private void saveAndClose() {
        Config cfg = KeepSprintMod.CONFIG;
        cfg.keepSprintEnabled    = keepSprintEnabled;
        cfg.omniSprintEnabled    = omniSprintEnabled;
        cfg.omniSprintMultiplier = omniSprintMultiplier;
        cfg.fastPlaceEnabled     = fastPlaceEnabled;
        cfg.blockReachEnabled    = blockReachEnabled;
        cfg.blockReachDistance   = blockReachDistance;
        cfg.timerEnabled         = timerEnabled;
        cfg.timerMultiplier      = timerMultiplier;
        cfg.save();
        mc.displayGuiScreen(null);
    }

    // --- Helpers ---

    private double fractionToValue(int mouseX, int sx, double min, double max) {
        return SliderMath.fractionToValue(mouseX, sx, sliderW, min, max);
    }

    private static double clampOmni(double v)  { return SliderMath.clampOmni(v); }
    private static double clampReach(double v) { return SliderMath.clampReach(v); }
    private static double clampTimer(double v) { return SliderMath.clampTimer(v); }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
