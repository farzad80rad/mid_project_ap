package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;


/**
 * this is a TextField class with hover effect.
 * the foreground of leading title is gray.
 */
public class HoverTextField extends JTextField{

    /**
     * simple constructor.
     * @param title the string which is wrote on textField when there is no other text.
     * @param currentString the current text .
     */
    public HoverTextField (String title, String currentString)
    {
        super();
        if(currentString.equals(title)) {
            setText(title);
            setForeground(Color.GRAY);
        }
        else
            setText(currentString);

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if(getForeground() == Color.GRAY)
                    setText("");
                setForeground(new JTextField().getForeground());
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(getText().length() == 0) {
                    setText(title);
                    setForeground(Color.GRAY);
                }
            }
        });

    }
}
