package frame;

import helpers.Koneksi;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

public class KabupatenViewFrame {
    private JTable table1;
    private JTextField textField1;
    private JButton cariButton;
    private JButton tambahButton;
    private JButton ubahButton;
    private JButton hapusButton;
    private JButton tutupButton;
    private JButton cetakButton;
    private JButton batalButton;

    public KabupatenViewFrame(){
        tutupButton.addActionListener(e -> {
            dispose();
        });
        batalButton.addActionListener(e -> {
            isiTable();
        });
        addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e){
                isiTable();
            }
        });
        tambahButton.addActionListener(e -> {
            KabupatenInputFrame inputFrame = new KabupatenInputFrame();
            inputFrame.setVisible(true);
        });
        cariButton.addActionListener(e -> {
            if (textField1.getText().equals("")){
                JOptionPane.showMessageDialog(null,"Isi kata kunci pencarian","Validasi kata kunci kosong",JOptionPane.WARNING_MESSAGE);
                textField1.requestFocus();
                return;
            }
            Connection c = Koneksi.getConnection();
            String keyword = "%" + textField1.getText() + "%";
            String searchSQL = "SELECT K.*,B.nama_provinsi AS nama_provinsi FROM kabupaten K LEFT JOIN provinsi B ON K.Id_provinsi = B.id WHERE K.nama_provinsi like ? OR B.nama_provinsi like ?";
            try {
                PreparedStatement ps = c.prepareStatement(searchSQL);
                ps.setString(1,keyword);
                ps.setString(2,keyword);
                ResultSet rs = ps.executeQuery();
                DefaultTableModel dtm = (DefaultTableModel) table1.getModel();
                dtm.setRowCount(0);
                Object[] row = new Object[6];
                while (rs.next()){
                    row[0] = rs.getInt("Id_kabupaten");
                    row[1] = rs.getString("nama_kabupaten");
                    row[2] = rs.getString("nama_provinsi");
                    row[3] = rs.getString("klasifikasi");
                    row[4] = rs.getInt("jumlah_kecamatan");
                    row[5] = rs.getDouble("luas_wilayah");
                    row[6] = rs.getDouble("email");
                    row[7] = rs.getDouble("tanggal_berdiri");
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
            KabupatenInputFrame inputFrame = new KabupatenInputFrame();
            inputFrame.setId(id);
            inputFrame.isiKomponen();
            inputFrame.setVisible(true);
        });
        hapusButton.addActionListener(e -> {
            int barisTerpilih = table1.getSelectedRow();
            if (barisTerpilih < 0){
                JOptionPane.showMessageDialog(null,"Pilih data dulu");
                return;
            }
            int pilihan = JOptionPane.showConfirmDialog(null,"Yakin mau dihapus ?","Konfirmasi Hapus",JOptionPane.YES_NO_OPTION);
            if (pilihan ==0){
                TableModel tm = table1.getModel();
                int id = Integer.parseInt(tm.getValueAt(barisTerpilih,0).toString());
                Connection c = Koneksi.getConnection();
                String deleteSQL = "delete from kabupaten where Id_kabupaten = ?";
                try {
                    PreparedStatement ps = c.prepareStatement(deleteSQL);
                    ps.setInt(1,id);
                    ps.executeUpdate();
                } catch (SQLException ex){
                    throw new RuntimeException(ex);
                }
            }
        });
        isiTable();
        init();
    }

    public void init(){
        setContentPane(mainPanel);
        setTitle("Data Kabupaten");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void isiTable(){
        Connection c = Koneksi.getConnection();
        String selectSQL = "SELECT K.*,B.nama_provinsi AS nama_provinsi FROM kabupaten K LEFT JOIN provinsi B ON K.Id_provinsi = B.id";
        try {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(selectSQL);
            String header[] = {"id","Nama Kabupaten","Nama Provinsi","Klasifikasi","Jumlah Kecamatan","Luas Wilayah"};
            DefaultTableModel dtm = new DefaultTableModel(header,0);
            table1.setModel(dtm);
            table1.getColumnModel().getColumn(0).setMaxWidth(32);
            table1.getColumnModel().getColumn(1).setMinWidth(150);
            table1.getColumnModel().getColumn(2).setMinWidth(150);

            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
            table1.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
            table1.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

            Object[] row = new Object[6];
            while (rs.next()){

                NumberFormat nf = NumberFormat.getInstance(Locale.US);
                String rowKecamatan = nf.format(rs.getInt("jumlah_kecamatan"));
                String rowLuas = String.format("%,.2f", rs.getDouble("luas_wilayah"));

                row[0] = rs.getInt("Id_kabupaten");
                row[1] = rs.getString("nama_kabupaten");
                row[2] = rs.getString("nama_provinsi");
                row[3] = rs.getString("klasifikasi");
                row[4] = rowKecamatan;
                row[5] = rowLuas;
                dtm.addRow(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    }

