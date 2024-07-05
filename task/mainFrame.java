import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class mainFrame {
    private Object[][] data = new Object[0][5];
    private gasolineCompany company;
    private int row;

    public mainFrame() {
        company = new gasolineCompany();
        Object[][] swata = {
                {"1", "NoIl", "12", "13", "14"},
                {"2", "NoIl", "17", "18", "19"},
                {"3", "NoIl", "22", "23", "44"}
        };

        setData(swata, 3);
        swata = null;
        swata = getData(company);



        String[] columnNames = {"№", "Компания", "Номер АЗС", "Номер колонки", "Марка топлива"};
        data = getData(company);
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);

        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setPreferredWidth(Math.max(table.getColumnModel().getColumn(i).getPreferredWidth(), table.getColumnModel().getColumn(i).getWidth()));
        }

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(950, 800));

        JFrame frame = new JFrame("Gasoline Company");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setResizable(false);

        JPanel tablePanel = new JPanel();
        tablePanel.setBackground(Color.darkGray);
        tablePanel.add(tableScrollPane);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.darkGray);
        buttonPanel.setLayout(new GridLayout(3, 3));
        buttonPanel.setPreferredSize(new Dimension(900, 200));

        buttons(buttonPanel, table);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(tablePanel, BorderLayout.CENTER);
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        frame.getContentPane().setBackground(Color.darkGray);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveDataToFile();
                company.destroy();
                company = null;
                clearData();
            }
        });
    }

    private void buttons(JPanel buttonPanel, JTable table){
        JButton fileChooserButton = new JButton("Загрузить данные из файла");
        fileChooserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("."));
                fileChooser.setFileFilter(new FileNameExtensionFilter("TXT files", "txt"));
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                    getDataFromFile(selectedFile);
                    updateTable(table);
                }
            }
        });
        buttonPanel.add(fileChooserButton);

        JButton btnSaveData = new JButton("Сохранить данные");
        btnSaveData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDataToFile();
            }
        });
        buttonPanel.add(btnSaveData);

        JButton deleteStation = new JButton("Удалить АЗС");
        deleteStation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                company.delete();
                data = getData(company);
                updateTable(table);
            }
        });
        buttonPanel.add(deleteStation);

        JButton deletePump = new JButton("Удалить колонку");
        deletePump.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] gasolineStations = new String[0];
                gasolineStation current = company.getHead();
                while (current != null) {
                    String[] newGasolineStations = new String[gasolineStations.length + 1];
                    for (int i = 0; i < gasolineStations.length; i++) {
                        newGasolineStations[i] = gasolineStations[i];
                        gasolineStations[i] = null;
                    }
                    newGasolineStations[gasolineStations.length] = Integer.toString(current.getGasolineStationNumber());
                    gasolineStations = new String[newGasolineStations.length];
                    for (int i = 0; i < gasolineStations.length; i++) {
                        gasolineStations[i] = newGasolineStations[i];
                        newGasolineStations[i] = null;
                    }
                    current = current.getNext();
                }

                JComboBox<String> gasolineStationBox = new JComboBox<>(gasolineStations);
                JPanel panel = new JPanel();
                panel.add(gasolineStationBox);

                int result = JOptionPane.showConfirmDialog(null, panel, "Выберите АЗС", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String selectedGasStationName = (String) gasolineStationBox.getSelectedItem();
                    String columnNumberInput = JOptionPane.showInputDialog(null, "Введите номер колонки:", "Ввод данных", JOptionPane.PLAIN_MESSAGE);
                    String fuelTypeInput = JOptionPane.showInputDialog(null, "Введите марку топлива:", "Ввод данных", JOptionPane.PLAIN_MESSAGE);
                    if (columnNumberInput != null && !columnNumberInput.isEmpty() && fuelTypeInput != null && !fuelTypeInput.isEmpty()) {
                        int columnNumber = Integer.parseInt(columnNumberInput);
                        gasolineStation gasStation = company.getGasolineStation(Integer.parseInt(selectedGasStationName));
                        gasStation.delete(columnNumber, fuelTypeInput);
                        data = getData(company);
                        updateTable(table);
                    }
                }
            }
        });
        buttonPanel.add(deletePump);

        JButton addStation = new JButton("Добавить АЗС");
        addStation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String gasolineStationNumber = JOptionPane.showInputDialog(null, "Введите АЗС:", "Ввод данных", JOptionPane.PLAIN_MESSAGE);
                company.add(Integer.parseInt(gasolineStationNumber));
                data = getData(company);
                updateTable(table);
            }
        });
        buttonPanel.add(addStation);

        JButton addPump = new JButton("Добавить колонку");
        addPump.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] gasolineStations = new String[0];
                gasolineStation current = company.getHead();
                if (current != null) {
                    while (current != null) {
                        String[] newGasolineStations = new String[gasolineStations.length + 1];
                        for (int i = 0; i < gasolineStations.length; i++) {
                            newGasolineStations[i] = gasolineStations[i];
                            gasolineStations[i] = null;
                        }
                        newGasolineStations[gasolineStations.length] = Integer.toString(current.getGasolineStationNumber());
                        gasolineStations = new String[newGasolineStations.length];
                        for (int i = 0; i < gasolineStations.length; i++) {
                            gasolineStations[i] = newGasolineStations[i];
                            newGasolineStations[i] = null;
                        }
                        current = current.getNext();
                    }


                    JComboBox<String> gasolineStationBox = new JComboBox<>(gasolineStations);
                    JPanel panel = new JPanel();
                    panel.add(gasolineStationBox);

                    int result = JOptionPane.showConfirmDialog(null, panel, "Выберите АЗС", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        String selectedGasStationName = (String) gasolineStationBox.getSelectedItem();
                        gasolineStation gasStation = company.getGasolineStation(Integer.parseInt(selectedGasStationName));

                        JPanel cpanel = new JPanel();
                        cpanel.setLayout(new GridLayout(3, 2));

                        JTextField columnNumberField = new JTextField(10);
                        JTextField fuelTypeField = new JTextField(10);
                        JTextField bOrA = new JTextField(10);
                        cpanel.add(new JLabel("Номер колонки:"));
                        cpanel.add(columnNumberField);
                        cpanel.add(new JLabel("Марка топлива:"));
                        cpanel.add(fuelTypeField);
                        cpanel.add(new JLabel("До или после(1 - до; 2 - после)"));
                        cpanel.add(bOrA);

                        int option = JOptionPane.showConfirmDialog(null, cpanel, "Выберите место", JOptionPane.OK_CANCEL_OPTION);
                        pumpStation place = null;
                        if (option == JOptionPane.OK_OPTION) {
                            String pumpNumber = columnNumberField.getText();
                            String fuelType = fuelTypeField.getText();
                            place = gasStation.getPump(Integer.parseInt(pumpNumber), fuelType);
                            if (place == null){
                                JOptionPane.showMessageDialog(null, "Такой колонки не найдено, будет добавлено в конец");
                            }
                        }
                        String columnNumberInput = JOptionPane.showInputDialog(null, "Введите номер колонки:", "Ввод данных", JOptionPane.PLAIN_MESSAGE);
                        String fuelTypeInput = JOptionPane.showInputDialog(null, "Введите марку топлива:", "Ввод данных", JOptionPane.PLAIN_MESSAGE);
                        if (columnNumberInput != null && !columnNumberInput.isEmpty() && fuelTypeInput != null && !fuelTypeInput.isEmpty()) {
                            int columnNumber = Integer.parseInt(columnNumberInput);
                            gasStation.add(columnNumber, fuelTypeInput, Integer.parseInt(bOrA.getText()), place);
                            data = getData(company);
                            updateTable(table);
                        }
                    }
                }
            }
        });
        buttonPanel.add(addPump);

        JButton searchButton = new JButton("Найти колонку");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(2, 2));

                JTextField columnNumberField = new JTextField(10);
                JTextField fuelTypeField = new JTextField(10);
                panel.add(new JLabel("Номер колонки:"));
                panel.add(columnNumberField);
                panel.add(new JLabel("Марка топлива:"));
                panel.add(fuelTypeField);

                int option = JOptionPane.showConfirmDialog(null, panel, "Найти колонку", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String columnNumber = columnNumberField.getText();
                    String fuelType = fuelTypeField.getText();

                    Object[] columnFound = findPumps(Integer.parseInt(columnNumber), fuelType);

                    if (columnFound != null) {
                        JOptionPane.showMessageDialog(null, "Такая колонка есть на АЗС " + Arrays.toString(columnFound));
                    } else {
                        JOptionPane.showMessageDialog(null, "Такой колонки нет");
                    }
                }
            }
        });

        buttonPanel.add(searchButton);

        JButton deleteAll = new JButton("Удалить всё");
        deleteAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                company.destroy();
                clearData();
                data = getData(company);
                updateTable(table);
            }
        });
        buttonPanel.add(deleteAll);

        JButton changeCompanyName = new JButton("Изменить имя компании");
        changeCompanyName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newCompanyName = JOptionPane.showInputDialog(buttonPanel, "Введите новое имя компании:");
                if (newCompanyName != null && !newCompanyName.isEmpty()) {
                    company.setCompanyName(newCompanyName);
                    data = getData(company);
                    updateTable(table);
                }
            }
        });
        buttonPanel.add(changeCompanyName);

    }

    private Object[] findPumps(int pupmNumber, String fuelGrade){
        Object[] stations = new Object[0];
        gasolineStation current = company.getHead();
        while(current != null){
            if (current.find(pupmNumber, fuelGrade) != null){

                Object[] newStations = new Object[stations.length + 1];
                for (int i = 0; i < stations.length; i++){
                    newStations[i] = stations[i];
                    stations[i] = null;
                }
                newStations[stations.length] = current.getGasolineStationNumber();
                stations = new Object[newStations.length];
                for (int i = 0; i < newStations.length; i++){
                    stations[i] = newStations[i];
                    newStations[i] = null;
                }
            }
            current = current.getNext();
        }
        if (stations.length == 0){
            return null;
        }
        return stations;
    }

    private Object[][] getData(gasolineCompany company) {
        if (company == null) {
            return null;
        }
        int rows = 0;
        Object[][] data = new Object[0][5];
        gasolineStation stationCurrent = company.getHead();
        while (stationCurrent != null) {
            pumpStation pumpCurrent = stationCurrent.getHead();
            boolean flag = true;
            while (pumpCurrent != null) {
                flag = false;
                rows++;
                Object[][] newData = new Object[rows][5];
                System.arraycopy(data, 0, newData, 0, data.length);
                newData[rows - 1][0] = String.valueOf(rows);
                newData[rows - 1][1] = company.getCompanyName();
                newData[rows - 1][2] = String.valueOf(stationCurrent.getGasolineStationNumber());
                newData[rows - 1][3] = String.valueOf(pumpCurrent.getPumpNumber());
                newData[rows - 1][4] = pumpCurrent.getFuelGrade();
                data = newData;
                pumpCurrent = pumpCurrent.getNext();
            }
            if (flag){
                rows++;
                Object[][] newData = new Object[rows][5];
                System.arraycopy(data, 0, newData, 0, data.length);
                newData[rows - 1][0] = String.valueOf(rows);
                newData[rows - 1][1] = company.getCompanyName();
                newData[rows - 1][2] = String.valueOf(stationCurrent.getGasolineStationNumber());
                newData[rows - 1][3] = " ";
                newData[rows - 1][4] = " ";
                data = newData;
            }
            stationCurrent = stationCurrent.getNext();
        }
        this.row = rows;
        return data;
    }

    private void clearData(){
        for (int i = 0; i < this.row; i++){
            this.data[i][0] = null;
            this.data[i][1] = null;
            this.data[i][2] = null;
            this.data[i][3] = null;
            this.data[i][4] = null;
        }
        this.data = null;
    }

    private void getDataFromFile(File selectedFile){
        clearData();
        company.destroy();
        try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))){
            Object[][] newData = new Object[0][5];
            String line;
            int i = 0;
            while ((line = br.readLine())!= null) {
                String[] columns = line.split(";");
                if (columns.length!= 4) {
                    System.err.println("Invalid line format: " + line);
                    continue;
                }
                Object[][] temp = new Object[newData.length + 1][5];
                System.arraycopy(newData, 0, temp, 0, newData.length);
                temp[newData.length][0] = ++i;
                temp[newData.length][1] = columns[0];
                temp[newData.length][2] = columns[1];
                temp[newData.length][3] = columns[2];
                temp[newData.length][4] = columns[3];
                newData = temp;
            }
            setData(newData, newData.length);
            data = getData(company);
        }catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private void setData(Object[][] data, int row) {
        if (data == null) {
            return;
        }
        String companyName = "None";
        company.destroy();
        for (int i = 0; i < row; i++) {
            companyName = (String) data[i][1];
            int gasolineStationNumber = Integer.parseInt((String) data[i][2]);
            int pumpNumber = Integer.parseInt((String) data[i][3]);
            String fuelGrade = (String) data[i][4];
            gasolineStation station = company.getGasolineStation(gasolineStationNumber);
            if (station == null){
                company.add(gasolineStationNumber);
                station = company.getGasolineStation(gasolineStationNumber);
            }
            if (station.find(pumpNumber, fuelGrade) == null) {
                station.add(pumpNumber, fuelGrade, 3, null);
            }
        }
        company.setCompanyName(companyName);
    }

    private void updateTable(JTable table) {
        Object[][] data = getData(company);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setDataVector(data, new Object[]{"№", "Компания", "Номер АЗС", "Номер колонки", "Марка топлива"});
        table.repaint();
    }

    private void saveDataToFile() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String fileName = "saved_data_" + formatter.format(now) + ".txt";

        try (FileWriter writer = new FileWriter(fileName)) {
            for (int i = 0; i < data.length; i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 1; j < data[i].length; j++) {
                    line.append(data[i][j].toString()).append(";");
                }
                line.deleteCharAt(line.length() - 1);
                line.append("\n");
                writer.write(line.toString());
            }
            JOptionPane.showMessageDialog(null, "Данные сохранены в файл " + fileName);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Ошибка при сохранении данных: " + ex.getMessage());
        }
    }
}
