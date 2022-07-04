package frame;

import helpers.ComboBox;
import helpers.Koneksi;

import javax.swing.*;
import java.sql.*;

public class KabupatenInputFrame {
    private JTextField textField1;
    private JTextField namakabupaten;
    private JComboBox comboBox1;
    private JRadioButton tipeARadioButton;
    private JRadioButton tipeBRadioButton;
    private JTextField jumlahkecamatan;
    private JTextField luaswilayah;
    private JTextField email;
    private JTextField tanggalberdiri;
    private JButton batalButton;
    private JButton simpanButton;
    private JPanel mainPanel;

    private int id;

    public void setId(int id){
        this.id = id;
    }

    public KabupatenInputFrame(){
        batalButton.addActionListener(e -> {
            dispose();
        });
        simpanButton.addActionListener(e ->{
            String nama = namakabupaten.getText();
            if (nama.equals("")){
                JOptionPane.showMessageDialog(null,"Isi Nama Kabupaten","Validasi kata kunci kosong",JOptionPane.WARNING_MESSAGE);
                namakabupaten.requestFocus();
                return;
            }

            ComboBox item = (ComboBox) comboBox1.getSelectedItem();
            int provinsiId = item.getValue();
            if (provinsiId == 0){
                JOptionPane.showMessageDialog(null,"Pilih Provinsi","Validasi ComboBox",JOptionPane.WARNING_MESSAGE);
                comboBox1.requestFocus();
                return;
            }
            String klasifikasi = "";
            if (tipeARadioButton.isSelected()){
                klasifikasi = "Tipe A";
            } else if (tipeBRadioButton.isSelected()){
                klasifikasi = "Tipe B";
            } else {
                JOptionPane.showMessageDialog(null,"Pilih klasifikasi","Validasi Data Kosong",JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (jumlahkecamatan.getText().equals("")){
                jumlahkecamatan.setText("0");
            }
            int jumlahkecamatan = Integer.parseInt(jumlahkecamatan.getText());
            if (jumlahkecamatan == 0){
                JOptionPane.showMessageDialog(null,"Isi Jumlah Kecamatan","Validasi Data Kosong",JOptionPane.WARNING_MESSAGE);
                jumlahkecamatan.requestFocus();
                return;
            }

            if (luaswilayah.getText().equals("")){
                luaswilayah.setText("0");
            }

            double luaswilayah = Float.parseFloat(luaswilayah.getText());
            if (luaswilayah == 0){
                JOptionPane.showMessageDialog(null,"Isi Luas","Validasi Data Kosong",JOptionPane.WARNING_MESSAGE);
                luaswilayah.requestFocus();
                return;
            }

            String email = email.getText();
            if (!email.contains("@") || !email.contains(".")){
                JOptionPane.showMessageDialog(null,"Isi dengan email valid","Validasi Email",JOptionPane.WARNING_MESSAGE);
                email.requestFocus();
                return;
            }

            Connection c = Koneksi.getConnection();
            PreparedStatement ps;
            try {
                if (id == 0) {
                    String cekSQL = "SELECT * FROM kabupaten WHERE nama_kabupaten= ?";
                    ps = c.prepareStatement(cekSQL);
                    ps.setString(1,nama);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null,"Data sama sudah ada");
                    } else {
                        String insertSQL = "INSERT INTO kabupaten (Id_kabupaten,nama_kabupaten,Id_profinsi,klasifikasi,jumlah_kecamatan,luas_wilayah,email,tanggal_berdiri) VALUES (NULL, ?, ?, ?, ?, ?,?,?)";
                        ps = c.prepareStatement(insertSQL);
                        ps.setString(1,nama);
                        ps.setInt(2,provinsiId);
                        ps.setString(3,klasifikasi);
                        ps.setInt(4,jumlahkecamatan);
                        ps.setDouble(5,luaswilayah);
                        ps.executeUpdate();
                        dispose();
                    }

                } else {
                    String cekSQL = "SELECT * FROM kabupaten WHERE nama_kabupaten= ? AND Id_kabupaten != ?";
                    ps = c.prepareStatement(cekSQL);
                    ps.setString(1,nama);
                    ps.setInt(2,id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null,"Data sama sudah ada");
                    } else {
                        String updateSQL = "UPDATE kabupaten SET nama_kabupaten= ?, Id_provinsi= ?, klasifikasi = ?, jumlah_kecamatan = ?, luas_wilayah=?,email=?,tanggal_berdiri=?  WHERE Id_kabupaten= ?";
                        ps = c.prepareStatement(updateSQL);
                        ps.setString(1,nama);
                        ps.setInt(2,provinsiId);
                        ps.setString(3,klasifikasi);
                        ps.setInt(4,jumlahkecamatan);
                        ps.setDouble(5,luaswilayah);
                        ps.setInt(6,id);
                        ps.executeUpdate();
                        dispose();
                    }

                }

            } catch (SQLException ex) {
                throw  new RuntimeException(ex);
            }
        });
        kustomisasiKomponen();
        init();
    }
    public void isiKomponen(){
        Connection c = Koneksi.getConnection();
        String findSQL = "SELECT * FROM kabupaten WHERE Id_kabupaten= ?";
        PreparedStatement ps = null;
        try {
            ps = c.prepareStatement(findSQL);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                textField1.setText(String.valueOf(rs.getInt("Id_kabupaten")));
                namakabupaten.setText(rs.getString("nama_kabupaten"));
                jumlahkecamatan.setText(String.valueOf(rs.getInt("jumlah_wilayah")));
                luaswilayah.setText(String.valueOf(rs.getDouble("luas_wilayah")));
                int kabupatenId = rs.getInt("Id_provinsi");
                for (int i = 0; i < comboBox1.getItemCount(); i++){
                    comboBox1.setSelectedIndex(i);
                    ComboBox item = (ComboBox) comboBox1.getSelectedItem();
                    if (kabupatenId == item.getValue()){
                        break;
                    }
                }
                String klasifikasi = rs.getString("klasifikasi");
                if (klasifikasi != null){
                    if (klasifikasi.equals("TIPE A")){
                        tipeARadioButton.setSelected(true);
                    } else if (klasifikasi.equals("TIPE B")){
                        tipeBRadioButton.setSelected(true);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void init(){
        setContentPane(mainPanel);
        setTitle("Input Kabupaten");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void kustomisasiKomponen(){
        Connection c = Koneksi.getConnection();
        String selectSQL = "SELECT * FROM provinsi ORDER BY nama_provinsi";
        try {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(selectSQL);
            comboBox1.addItem(new ComboBox(0,"Pilih Provinsi"));
            while (rs.next()){
                comboBox1.addItem(new ComboBox(rs.getInt("Id_provinsi"),rs.getString("nama_provinsi")));
            }
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
        klasifikasiButtonGoup = new ButtonGroup();
        klasifikasiButtonGoup.add(tipeARadioButton);
        klasifikasiButtonGoup.add(tipeBRadioButton);
    }
}
