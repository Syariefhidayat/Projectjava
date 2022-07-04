package frame;

import helpers.Koneksi;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

public class ProvinsiViewFrame extends JFrame{
    private JPanel mainPanel;
    private JTextField textField1;
    private JButton cariButton;
    private JButton tambahButton;
    private JButton ubahButton;
    private JButton hapusButton;
    private JButton batalButton;
    private JButton cetakButton;
    private JPanel cariPanel;
    private JScrollPane viewScrollPane;
    private JTable table1;
    private JButton tutupButton;
    private JPanel buttonPanel;

    public ProvinsiViewFrame(){
        tutupButton.addActionListener(e -> {
            dispose();
        });
        batalButton.addActionListener(e -> {
            isiTabel();
        });
        addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e){
                isiTabel();
            }
        });
        tambahButton.addActionListener(e -> {
            ProvinsiInputFrame inputFrame = new ProvinsiInputFrame();
//            inputFrame.setViseble(true);
//            inputFrame.setVisible(true);
        });
        cariButton.addActionListener(e -> {
            if (textField1.getText().equals("")){
                JOptionPane.showMessageDialog(null,"Isi kata kunci pencarian","Validasi kata kunci kosong",JOptionPane.WARNING_MESSAGE);
                textField1.requestFocus();
                return;
            }
            Connection c = Koneksi.getConnection();
            String keyword = "%" + textField1.getText() + "%";
            String searchSQL = "select * from provinsi where nama_provinsi like ?";
            try {
                PreparedStatement ps = c.prepareStatement(searchSQL);
                ps.setString(1,keyword);
                ResultSet rs = ps.executeQuery();
                DefaultTableModel dtm = (DefaultTableModel) table1.getModel();
                dtm.setRowCount(0);
                Object[] row = new Object[2];
                while (rs.next()){
                    row[0] = rs.getInt("Id_provinsi");
                    row[1] = rs.getString("nama_provinsi");
                    dtm.addRow(row);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        ubahButton.addActionListener(e -> {
            int barisTerpilih = table1.getSelectedRow();
            if(barisTerpilih < 0) {
                JOptionPane.showMessageDialog(null,"Pilih data dulu");
                return;
            }
            TableModel tm = table1.getModel();
            int id = Integer.parseInt(tm.getValueAt(barisTerpilih,0).toString());
            ProvinsiInputFrame inputFrame = new ProvinsiInputFrame();
            inputFrame.setId(id);
            inputFrame.isiKomponen();
//            inputFrame.setVisible(true);
        });



        hapusButton.addActionListener(e -> {
            int barisTerpilih = table1.getSelectedRow();
            if (barisTerpilih < 0){
                JOptionPane.showMessageDialog(null,"Pilih data dulu");
                return;
            }
            int pilihan = JOptionPane.showConfirmDialog(null,"Yakin mau dihapus ?","Konfirmasi Hapus",JOptionPane.YES_NO_OPTION);
            if (pilihan == 0){
                TableModel tm = table1.getModel();
                int id = Integer.parseInt(tm.getValueAt(barisTerpilih,0).toString());
                Connection c = Koneksi.getConnection();
                String deleteSQL = "DELETE FROM provinsi WHERE Id_provinsi = ?";
                try {
                    PreparedStatement ps = c.prepareStatement(deleteSQL);
                    ps.setInt(1,id);
                    ps.executeUpdate();
                } catch (SQLException ex){
                    throw new RuntimeException(ex);
                }
            }
        });

        cetakButton.addActionListener(e -> {
            Connection c = Koneksi.getConnection();
            String selectSQL = "SELECT * FROM provinsi";
            Object[][] row;
            try {
                Statement s = c.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = s.executeQuery(selectSQL);
                rs.last();
                int jumlah = rs.getRow();
                row = new Object[jumlah][2];
                int i = 0;
                rs.beforeFirst();
                while (rs.next()){
                    row[i][0] = rs.getInt("Id_provinsi");
                    row[i][1] = rs.getString("nama_provinsi");
                    i++;
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            try {
                JasperReport jasperReport = JasperCompileManager.compileReport("/Users/Asus/IdeaProjects/");
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,null, new JasperDataSourceBuilder(row));
                JasperViewer viewer = new JasperViewer(jasperPrint, false);
                viewer.setVisible(true);
            } catch (JRException ex) {
                throw new RuntimeException(ex);
            }
        });
        isiTabel();
        init();
    }

    public void init(){
        setContentPane(mainPanel);
        setTitle("Data Provinsi");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void isiTabel(){
        Connection c = Koneksi.getConnection();
        String selectSQL = "select * from provinsi";
        try {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(selectSQL);
            String header[] = {"id","Nama Provinsi"};
            DefaultTableModel dtm = new DefaultTableModel(header,0);
            table1.setModel(dtm);
            Object[] row = new Object[2];
            while (rs.next()){
                row[0] = rs.getInt("Id_provinsi");
                row[1] = rs.getString("nama_provinsi");
                dtm.addRow(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
