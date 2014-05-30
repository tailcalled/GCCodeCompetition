package gccc;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {
	
	public MainPanel(final Competition competition) throws UnknownHostException {
		this.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets=new Insets(4,4,0,0);
		gc.anchor=GridBagConstraints.NORTHWEST;
		this.add(new JLabel("IP-Address:"), gc);
		gc.gridwidth=GridBagConstraints.REMAINDER;
		this.add(new JLabel(InetAddress.getLocalHost().getHostAddress()), gc);
		gc.gridwidth=1;
		this.add(new JLabel("Attempts:"), gc);
		JLabel countLabel = new JLabel(Integer.toString(competition.getQueue().getAllAttempts().size()));
		gc.gridwidth=GridBagConstraints.REMAINDER;
		this.add(countLabel, gc);
		competition.getQueue().addListener(new Runnable() {
			@Override
			public void run() {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						countLabel.setText(Integer.toString(competition.getQueue().getAllAttempts().size()));
						MainPanel.this.repaint();
					}
				});
			}
		});
	}

}
