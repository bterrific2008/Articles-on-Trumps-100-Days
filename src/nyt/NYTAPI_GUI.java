package nyt;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.json.JSONException;

public class NYTAPI_GUI extends JFrame implements ActionListener{

	JCheckBox[] fields;
	JLabel key_label, query_label, begin_date_label, end_date_label;
	JTextField key, query, begin_date, end_date;
	JButton start;

	// constructor
	public NYTAPI_GUI() {
		//GridLayout layout = new GridLayout(5,1);

		key_label = new JLabel("API Key:");
		key = new JTextField("");

		String fields_label[] = {"web_url", "snippet", "lead_paragraph", 
				"print_page", "blog", "source", "multimedia", "headline",
				"keywords", "pub_date", "document_type", "news_desk", "byline",
				"type_of_material", "_id", "word_count"};
		fields = new JCheckBox[fields_label.length];
		for(int i = 0; i<fields_label.length; i++){
			fields[i] = new JCheckBox(fields_label[i]);
		}

		query_label = new JLabel("Query:");
		query = new JTextField("");

		begin_date_label = new JLabel("Beginning Date:");
		begin_date = new JTextField();

		end_date_label = new JLabel("Ending Date:");
		end_date = new JTextField();

		start = new JButton("Start");
		start.addActionListener(this);

		this.add(key_label);
		this.add(key);

		for(int i = 0; i<fields_label.length; i++){
			this.add(fields[i]);
		}

		this.add(query_label);
		this.add(query);

		this.add(begin_date_label);
		this.add(begin_date);

		this.add(end_date_label);
		this.add(end_date);

		this.add(start);

		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		SequentialGroup fl_horizontal = layout.createSequentialGroup();
		ParallelGroup fl_h = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		SequentialGroup fl_vertical = layout.createSequentialGroup();
		ParallelGroup fl_v = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		fl_vertical.addGroup(fl_v);
		fl_horizontal.addGroup(fl_h);
		for(int i = 0; i<fields.length; i++){
			fl_h.addComponent(fields[i]);
			if(i<fields.length/2){
				fl_v.addComponent(fields[i]);
				fl_v.addComponent(fields[i%(fields.length/2)+fields.length/2]);
				System.out.println(i);
				System.out.println(i%(fields.length/2-1)+fields.length/2-1);
				fl_v = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
				fl_vertical.addGroup(fl_v);
			}
			if(i==fields.length/2-1){
				fl_h = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
				fl_horizontal.addGroup(fl_h);
			}
		}

		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(key_label)
								.addComponent(query_label)
								.addComponent(begin_date_label)
								.addComponent(end_date_label))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(key)
								.addComponent(query)
								.addComponent(begin_date)
								.addComponent(end_date)))
				.addGroup(fl_horizontal)
				.addComponent(start, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(key_label)
								.addComponent(key))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(query_label)
								.addComponent(query))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(begin_date_label)
								.addComponent(begin_date))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(end_date_label)
								.addComponent(end_date)))
				.addGroup(fl_vertical)
				.addComponent(start));


	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(start==e.getSource()){
			int fields_str_length = 0;
			for(int i = 0; i<fields.length; i++){
				if(fields[i].isSelected())
					fields_str_length++;
			}
			String[] fields_str = new String[fields_str_length];
			for(int i = 0; i<fields.length; i++){
				if(fields[i].isSelected())
					fields_str[i] = fields[i].getText();
			}
			NYT_search search = new NYT_search(key.getText(), query.getText(), begin_date.getText(), end_date.getText(), fields_str);
			try {
				search.startSearch();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Did this happen?");
		}
	}

	// creating the GUI
	private static void createAndShowGUI(){
		JFrame frame = new NYTAPI_GUI();
		frame.setMinimumSize(new Dimension(300, 300));
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// main method
	public static void main(String[] args) {
		createAndShowGUI();
	}

}
