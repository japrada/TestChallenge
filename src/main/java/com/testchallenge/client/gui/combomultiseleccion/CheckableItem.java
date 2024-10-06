
/**
 * @aterai (TERAI Atsuhiro)
 * 
 * Java Swing Tips: https://java-swing-tips.blogspot.com/2016/07/select-multiple-jcheckbox-in-jcombobox.html
 * License: https://creativecommons.org/publicdomain/zero/1.0/
 */

package com.testchallenge.client.gui.combomultiseleccion;

public class CheckableItem {

    public final String text;
    private boolean selected;

    public CheckableItem(String text, boolean selected) {
        this.text = text;
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return text;
    }
}
