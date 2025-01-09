package kz.tamur.guidesigner.expr;

import java.util.ArrayList;

import javax.swing.JOptionPane;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class WndTableEditor extends javax.swing.JDialog {               
    private javax.swing.JButton BtnUP;
    private javax.swing.JButton BtnDOWN;
    private javax.swing.JButton BtnOK;
    private javax.swing.JButton BtnCANSEL;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    
    private Object[][] model = null;
    private ArrayList<Integer> pos_columns = new ArrayList<Integer>();
    private int CheckHash;
    private ActionListenerRt<Boolean, int[]> al;
    private String title = "";
    
    public WndTableEditor(java.awt.Frame parentWnd, Object[][] inColumnsInfo, String _title, ActionListenerRt<Boolean, int[]> _al) {
    	super(parentWnd, true);
    	init(inColumnsInfo, _title, _al);
    }
    
    public WndTableEditor(java.awt.Dialog parentWnd, Object[][] inColumnsInfo, String _title, ActionListenerRt<Boolean, int[]> _al) {
    	super(parentWnd, true);
    	init(inColumnsInfo, _title, _al);
    }

    public void init(Object[][] inColumnsInfo, String _title, ActionListenerRt<Boolean, int[]> _al) {
    	title = _title;
    	al = _al;

    	model = inColumnsInfo;

        for (int i = 1; i <= model.length; i++) {
            pos_columns.add(i);
        }

        CheckHash = pos_columns.hashCode();
        initComponents();
        RefreshModel();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
                   
    private void initComponents() {

        BtnUP = new javax.swing.JButton();
        BtnDOWN = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        BtnOK = new javax.swing.JButton();
        BtnCANSEL = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        //setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Редактировать таблицу");
        //setModalExclusionType(java.awt.Dialog.ModalExclusionType.NO_EXCLUDE);
        setResizable(false);
        //setType(java.awt.Window.Type.UTILITY);
        //setLocationByPlatform(true);
    	//this.setLocationRelativeTo(null);
        

        BtnUP.setText("Вверх");
        BtnUP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	BtnUPAction(evt);
            }
        });

        BtnDOWN.setText("Вниз");
        BtnDOWN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	BtnDOWNAction(evt);
            }
        });

        jTable1.setDragEnabled(true);
        jTable1.setEditingColumn(0);
        jTable1.setEditingRow(0);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(jTable1);

        BtnOK.setText("ОК");
        BtnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	BtnOKAction(evt);
            }
        });

        BtnCANSEL.setText("Отмена");
        BtnCANSEL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	BtnCANSELAction(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(title);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BtnDOWN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(BtnUP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(BtnOK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(BtnCANSEL, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BtnUP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BtnDOWN)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 189, Short.MAX_VALUE)
                        .addComponent(BtnCANSEL)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BtnOK))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>                        

    public Integer[] getNewPos() {
        return pos_columns.toArray(new Integer[pos_columns.size()]);
    }

    public final void RefreshModel() {
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                model,
                new String[]{
            "наименование", "тип данных"
        }) {
            Class[] types = new Class[]{
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean[]{
                false, false
            };

			@Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
    }

    public void MoveColumn(int in, int to) {
    	if (in != -1 && to != -1) {
	        Object[] tmp = model[in];
	        model[in] = model[to];
	        model[to] = tmp;
	
	        pos_columns.set(in, pos_columns.get(in) ^ pos_columns.get(to));
	        pos_columns.set(to, pos_columns.get(to) ^ pos_columns.get(in));
	        pos_columns.set(in, pos_columns.get(in) ^ pos_columns.get(to));
	
	        RefreshModel();
	
	        jTable1.setRowSelectionInterval(to, to);
    	}
    }

    private void BtnUPAction(java.awt.event.ActionEvent evt) {   
        int in = jTable1.getSelectedRow();
        int to = jTable1.getSelectedRow() - 1;
        if (in > 0) {
            MoveColumn(in, to);
        }
    }                                        

    private void BtnDOWNAction(java.awt.event.ActionEvent evt) {                                         
        int in = jTable1.getSelectedRow();
        int to = jTable1.getSelectedRow() + 1;
        if (model.length > to) {
            MoveColumn(in, to);
        }
    }                                        

    private void BtnCANSELAction(java.awt.event.ActionEvent evt) {                                         
        this.setVisible(false);
        this.dispose();
    }                                        

    private void BtnOKAction(java.awt.event.ActionEvent evt) {                                         
        final javax.swing.JDialog frame = this;

        if (CheckHash == pos_columns.hashCode()) {
            this.setVisible(false);
            this.dispose();
        } else {
        	int[] outcol = new int[pos_columns.size()];
        	int i = 0;
        	for (int j : pos_columns){
        		outcol[i++] = j;
        	}
            if (al.action(outcol)) {
                this.setVisible(false);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "неизвестная ошибка.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }                                        
             
}
