package aurelienribon.utils.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class SpinnerNullableFloatEditor extends JTextField {
	private final SpinnerNullableFloatModel model;

	public SpinnerNullableFloatEditor(final SpinnerNullableFloatModel model) {
		this.model = model;
		setHorizontalAlignment(SwingConstants.RIGHT);

		model.addChangeListener(new ChangeListener() {
			@Override public void stateChanged(ChangeEvent e) {
				update();
			}
		});

		addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				model.setValue(Float.parseFloat(getText()));
			}
		});

		update();
	}

	private void update() {
		if (model.getValue() == null) {
			setText("---");
		} else {
			setText(String.format(Locale.US, "%.2f", model.getValue()));
		}
	}
}
