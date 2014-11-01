package com.IYYX.cardboard.Helpers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import com.IYYX.cardboard.myAPIs.Model;
import com.IYYX.cardboard.myAPIs.ModelIO;

public class FilenameManager {
	
	static String objName=null;
	static Dimension controlPanelDimension;
	static Point controlPanelLocation;
	static Model[] origObj;
	
	public static void main(String[] args) {
		System.out.println("This is not a class used in Android.");
		showControlPanel();
		JFileChooser openObj=new JFileChooser();
		openObj.setDialogTitle("Choose *.obj File");
		openObj.setCurrentDirectory(new File("./assets/"));
		openObj.setFileFilter(new FileFilter(){
			public boolean accept(File f) {
				String[] tmp=f.getName().split("\\.");
				if(tmp.length==0) return false;
				if(f.isDirectory()) return true;
				if(tmp[tmp.length-1].equalsIgnoreCase("obj"))
					return true;
				else return false;
			}
			public String getDescription() {
				return "3D Model File (.obj)";
			}
		});
		int result = openObj.showOpenDialog(null);
		if(result!=JFileChooser.APPROVE_OPTION) System.exit(0);
		else {
			if(!openObj.getSelectedFile().getAbsolutePath().contains(new File("assets/").getAbsolutePath())) {
				System.err.println("Please select files ONLY in the project ./assets/ folder.");
				System.exit(0);
			}
			objName = openObj.getSelectedFile().getName();
		}
		obj=new Vector<Model>();
		origObj=readObj(objName);
		for(int i=0;i<origObj.length;i++) obj.add(origObj[i]);
		
		showModelNameList();
		showTexturePreview();
		showSearchBoxFrame();
	}
	
	static JFrame controlPanelFrame = null;
	static JFrame textureListFrame = null;
	static JFrame modelListFrame = null;
	static JFrame searchBoxFrame = null;
	static boolean alwaysOnTop=false;
	static JList<String> textureList = null;
	static JList<Model> modelList = null;
	static Vector<String> texturesRelativePath=new Vector<String>();
	static Vector<Model> obj=null;

	static Vector<String> filteredP=new Vector<String>();
	static Vector<Model> filteredM=new Vector<Model>();
	
	static JTextField textField=null;
	
	static void updateList(){
		if(textField==null) return;
		searchBoxFrame.pack();
		filteredP=new Vector<String>();
		filteredM=new Vector<Model>();
		if(texturesRelativePath!=null) {
			for(int i=0;i<texturesRelativePath.size();i++) {
				String str=texturesRelativePath.get(i);
				if(str.toLowerCase().contains(textField.getText().toLowerCase())) {
					filteredP.add(str);
				}
			}
		} else filteredP=null;
		if(obj!=null) {
			for(int i=0;i<obj.size();i++) {
				Model model=obj.get(i);
				if(model.name.toLowerCase().contains(textField.getText().toLowerCase())){
					filteredM.add(model);
				}
			}
		} else filteredM=null;
		if(textureList!=null) {
			textureList.setListData(filteredP);
			textureListFrame.repaint();
		}
		if(modelList!=null) {
			modelList.setListData(filteredM);
			modelListFrame.repaint();
		}
	}
	
	static void showSearchBoxFrame(){
		searchBoxFrame=new JFrame("Search Box");
		textField=new JTextField();
		textField.setSize(80, 60);
		searchBoxFrame.add(textField);
		textField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				updateList();
			}
			public void insertUpdate(DocumentEvent e) {
				updateList();
			}
			public void removeUpdate(DocumentEvent e) {
				updateList();
			}
		});
		searchBoxFrame.pack();
		searchBoxFrame.setVisible(true);
	}
	
	public static void infoBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
	
	static HashMap<String,String> bindingResult=new HashMap<String,String>();
	
	static void showControlPanel(){
		JFrame window=controlPanelFrame=new JFrame("Main Panel");
		JButton loadTexture=new JButton("Load Texture");
		JButton bindButton=new JButton("Bind");
		JButton quickBindButton=new JButton("Quick Bind");
		JButton saveButton=new JButton("Save Binding");
		JButton zipButton=new JButton("Update .objpp");
		final JButton stayOnTop=new JButton("Stay on top");

		final JFrame window2 = textureListFrame = new JFrame("Texture List");
		JPanel container = new JPanel();
		JScrollPane scrPane = new JScrollPane(container);
		textureList=new JList<String>(texturesRelativePath);
		scrPane.setBackground(Color.WHITE);
		container.setBackground(Color.WHITE);
		window2.add(scrPane);
		
		textureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		textureList.addListSelectionListener(new TextureNameListListener());
		
		quickBindButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(filteredP.size()!=1) {
					infoBox("Quick Binding is only available when:\nThere is one and only one item in the picture list.","ERROR");
					return;
				}
				final String tex=filteredP.get(0);
				for(Model m:filteredM) {
					bindingResult.put(m.name, tex);
					obj.remove(m);
					modelList.setListData(obj);
					currentModel=null;
					currentTextureName=null;
				}
				previewCanvas.repaint();
				textField.setText("");
				updateList();
			}
		});
		
		zipButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser saveObjpp=new JFileChooser();
				saveObjpp.setDialogTitle("Save As *.objpp File");
				saveObjpp.setCurrentDirectory(new File("./assets/"));
				saveObjpp.setFileFilter(new FileFilter(){
					public boolean accept(File f) {
						String[] tmp=f.getName().split("\\.");
						if(tmp.length==0) return false;
						if(f.isDirectory()) return true;
						if(tmp[tmp.length-1].equalsIgnoreCase("objpp"))
							return true;
						else return false;
					}
					public String getDescription() {
						return "Our Own Compressed Model File (.objpp)";
					}
				});
				int result = saveObjpp.showSaveDialog(controlPanelFrame);
				if(result==JFileChooser.APPROVE_OPTION) try {
					ModelIO.save(origObj,new FileOutputStream(saveObjpp.getSelectedFile()));
					infoBox("Created *.objpp file in PARTITIONED format.","Warning");
				}
				catch(IOException err) {
					infoBox("Failed! IOException occured!","Warning");
					err.printStackTrace();
				}
			}
		});
		
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(new File("./assets/TextureInfo/"+objName+".info").exists())
					infoBox("There already exists a configuration file\n for the selected Model.\nPlease be cautious!\n\nIf backup is needed, please DON\'t press OK,\n and please copy the file out, NOW!","Warning!");
				try {
					OutputStream ostream=new FileOutputStream("./assets/TextureInfo/"+objName+"-info");
					ObjectOutputStream writer=new ObjectOutputStream(ostream);
					writer.writeObject(bindingResult);
					writer.close();
					infoBox("Notice:\nInfo File saved as "+"./assets/TextureInfo/"+objName+"-info","INFORMATION!");
				} catch (IOException e1) {
					System.err.println("Cannot write Texture Inforamtion into Android Project!");
				}
			}
		});
		
		bindButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				final int texIndex=textureList.getSelectedIndex();
				final String tex=filteredP.get(texIndex);
				final int modIndex=modelList.getSelectedIndex();
				final Model mod=filteredM.get(modIndex);
				bindingResult.put(mod.name, tex);
				obj.remove(mod);
				modelList.setListData(obj);
				currentModel=null;
				currentTextureName=null;
				previewCanvas.repaint();
				updateList();
			}
		});
		
		stayOnTop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				alwaysOnTop = !alwaysOnTop;
				if(controlPanelFrame!=null) controlPanelFrame.setAlwaysOnTop(alwaysOnTop);
				if(textureListFrame!=null) textureListFrame.setAlwaysOnTop(alwaysOnTop);
				if(modelListFrame!=null) modelListFrame.setAlwaysOnTop(alwaysOnTop);
				if(previewFrame!=null) previewFrame.setAlwaysOnTop(alwaysOnTop);
				if(searchBoxFrame!=null) searchBoxFrame.setAlwaysOnTop(alwaysOnTop);
				if(alwaysOnTop) stayOnTop.setText("Don\'t stay top");
				else stayOnTop.setText("Stay on top");
			}
		});
		
		loadTexture.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser openObj=new JFileChooser();
				openObj.setMultiSelectionEnabled(true);
				openObj.setDialogTitle("Choose Image File");
				openObj.setCurrentDirectory(new File("./assets/"));
				openObj.setFileFilter(new FileFilter(){
					public boolean accept(File f) {
						return true;
					}
					public String getDescription() {
						return "Image File (*.*)";
					}
				});
				int result = openObj.showOpenDialog(controlPanelFrame);
				if(result!=JFileChooser.APPROVE_OPTION) return;
				else {
					File[] ans=openObj.getSelectedFiles();
					for(File f:ans){
						if(!f.getAbsolutePath().contains(new File("assets/").getAbsolutePath())) {
							System.err.println("Please select files ONLY in the project's ./assets/ folder.");
							return;
						}
						String tmp=getRelativePath(f,"./assets/");
						texturesRelativePath.add(tmp);
						textureList.setListData(texturesRelativePath);
						updateList();
						window2.pack();
					}
				}
			}
		});
		
		window.setLayout(new GridLayout(0,1));
		window.add(loadTexture);
		window.add(new JSeparator());
		window.add(zipButton);
		window.add(new JSeparator());
		window.add(bindButton);
		window.add(new JSeparator());
		window.add(quickBindButton);
		window.add(new JSeparator());
		window.add(saveButton);
		window.add(new JSeparator());
		window.add(stayOnTop);
		window.pack();
		window.setLocation(20, 15);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		controlPanelLocation=window.getLocation();
		controlPanelDimension=window.getSize();
		
		container.add(textureList);
		window2.pack();
		window2.setLocation(controlPanelLocation.x+0, controlPanelLocation.y+controlPanelDimension.height+10);
		window2.setVisible(true);
		window2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	static String getRelativePath(String path, String basePath) {
		return new File(basePath).toURI().relativize(new File(path).toURI()).getPath();
	}

	static String getRelativePath(File path, String basePath) {
		return new File(basePath).toURI().relativize(path.toURI()).getPath();
	}
	
	static JComponent previewCanvas = null;
	static JFrame previewFrame = null;
	
	static void showTexturePreview(){
		JFrame window=previewFrame=new JFrame("Result of current match");
		window.add(previewCanvas=new BinderCanvas());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setBounds(450, 150, 500, 500);
		window.setVisible(true);
	}
	
	static void showModelNameList(){
		JFrame window = modelListFrame = new JFrame("Model");
		JPanel container = new JPanel();
		JScrollPane scrPane = new JScrollPane(container);
		modelList=new JList<Model>(obj);
		scrPane.setBackground(Color.WHITE);
		container.setBackground(Color.WHITE);
		window.add(scrPane);
		
		modelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		modelList.addListSelectionListener(new ModelNameListListener());
		container.add(modelList);
		window.pack();
		window.setLocation(controlPanelLocation.x+controlPanelDimension.width+10, controlPanelLocation.y+0);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	static Model currentModel = null;
	
	static class ModelNameListListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent arg0) {
			JList<Model> list=(JList<Model>)arg0.getSource();
			if(list.getSelectedValue()!=null)
				currentModel=list.getSelectedValue();
			if(previewCanvas!=null) previewCanvas.repaint();
		}
	}
	
	static String currentTextureName = null;

	static class TextureNameListListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent arg0) {
			JList<String> list=(JList<String>)arg0.getSource();
			if(list.getSelectedValue()!=null)
				currentTextureName=list.getSelectedValue();
			if(previewCanvas!=null) previewCanvas.repaint();
		}
	}
	
	static Model[] readObj(String name){
		Model[] ans = null;
		try {
			System.out.print("Loading");
			ans = Model.readPartitionedModel(name, new TestCallback());
			System.out.println();
			System.out.println("Loading Completed.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ans;
	}

	static class BinderCanvas extends JComponent {
		
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			float w = 128;
			float h = 128;
			
			if(currentTextureName!=null) {
				MediaTracker mt = new MediaTracker(this);
				Image img1 = Toolkit.getDefaultToolkit().getImage("./assets/"+currentTextureName);
				mt.addImage(img1, 0);
				try {
					mt.waitForAll();
					w=img1.getWidth(this);
					h=img1.getHeight(this);
					float tmpH=h;
					if(h>512f) h=512f;
					w=w/tmpH*h;
				} catch (InterruptedException e) {
					e.printStackTrace();
					w=h=128f;
				}
				g2.drawImage(img1, 0, 0, (int)w, (int)h, this);
			}


			if(currentModel!=null) for (int i = 0; i < currentModel.fCount; i++) {
				int x1 = (int) (w * currentModel.textureUVs.get(i * 6 + 0));
				int y1 = (int) (h * currentModel.textureUVs.get(i * 6 + 1));
				int x2 = (int) (w * currentModel.textureUVs.get(i * 6 + 2));
				int y2 = (int) (h * currentModel.textureUVs.get(i * 6 + 3));
				int x3 = (int) (w * currentModel.textureUVs.get(i * 6 + 4));
				int y3 = (int) (h * currentModel.textureUVs.get(i * 6 + 5));
				g2.setColor(new Color(255, 0, 0));
				g2.drawLine(x1, y1, x2, y2);
				g2.drawLine(x2, y2, x3, y3);
				g2.drawLine(x3, y3, x1, y1);
			}
			g2.finalize();
			if(previewFrame!=null) {
				previewFrame.setSize((int)w, (int)h);
			}
		}
	}
}