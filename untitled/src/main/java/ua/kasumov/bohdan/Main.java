package ua.kasumov.bohdan;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        new IntroForm();
    }
}

class IntroForm extends JFrame {
    private JPanel contentPane;
    private JTextField numberTextField;
    private JButton introButton;
    private JLabel introLabel;

    public IntroForm() {
        createForm();
        introButton.addActionListener(e -> {
            try {
                int count = Integer.parseInt(numberTextField.getText());
                if (count == 0) throw new NumberFormatException();
                new SortForm(count);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(IntroForm.this, "Enter positive number");
            }

        });
        setTitle("Intro");
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    private void createForm() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(30, 30, 30, 30), -1, -1));
        Dimension buttonSize = new Dimension(80, 25);

        introLabel = new JLabel();
        introLabel.setText("How many numbers to display?");
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        labelPanel.add(introLabel);
        contentPane.add(labelPanel, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
                false));

        numberTextField = new JTextField();
        JPanel textFieldPanel = new JPanel(new BorderLayout());
        textFieldPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        textFieldPanel.add(numberTextField);
        contentPane.add(textFieldPanel, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, buttonSize, null, 0,
                false));

        introButton = new JButton();
        introButton.setText("Enter");
        introButton.setBackground(Color.BLUE);
        introButton.setForeground(Color.WHITE);
        contentPane.add(introButton, new GridConstraints(2, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, buttonSize, null, 0,
                false));
    }

}

class SortForm extends JFrame {
    private List<JButton> numberButtons = new ArrayList<>();
    private List<Integer> numbers = new ArrayList<>();
    private JPanel numberPanel;
    private boolean descending = true;

    public SortForm(int count) {
        setTitle("Sort");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        numberPanel = new JPanel();

        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapperPanel.add(numberPanel);

        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        generateNumbers(count);
        renderButtons();

        JPanel controlPanel = createControlPanel();

        add(mainPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);

        setVisible(true);
    }

    /**
     * Create and configure control panel with sort/reset buttons.
     *
     * @return JPanel object with control panel
     */
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton sortButton = new JButton("Sort");
        setUtilButtonProperties(sortButton);
        sortButton.addActionListener(e -> new Thread(() -> {
            quickSort(0, numbers.size() - 1);
            changeSortOrder();
        }).start());

        JButton resetButton = new JButton("Reset");
        setUtilButtonProperties(resetButton);
        resetButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(IntroForm::new);
        });

        controlPanel.add(sortButton);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(resetButton);
        return controlPanel;
    }

    /**
     * Set visual properties to util buttons (sort, reset).
     *
     * @param utilButton the button to apply properties to
     */
    private static void setUtilButtonProperties(JButton utilButton) {
        Dimension buttonSize = new Dimension(80, 25);
        utilButton.setBackground(Color.GREEN.darker());
        utilButton.setOpaque(true);
        utilButton.setForeground(Color.WHITE);
        utilButton.setFocusPainted(false);
        utilButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        utilButton.setSize(buttonSize);
        utilButton.setMaximumSize(buttonSize);
        utilButton.setPreferredSize(buttonSize);
        utilButton.setMinimumSize(buttonSize);
    }

    /**
     * Set visual properties to number buttons.
     *
     * @param numberButton the button to apply properties to
     */
    private static void setNumberButtonProperties(JButton numberButton) {
        Dimension buttonSize = new Dimension(80, 25);
        numberButton.setBackground(Color.BLUE);
        numberButton.setOpaque(true);
        numberButton.setForeground(Color.WHITE);
        numberButton.setFocusPainted(false);
        numberButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        numberButton.setSize(buttonSize);
        numberButton.setMaximumSize(buttonSize);
        numberButton.setPreferredSize(buttonSize);
        numberButton.setMinimumSize(buttonSize);
    }

    /**
     * Generate a list of random integer value. Must contain at least one values equals or less than 30.
     *
     * @param count - the number of random values to generate.
     */
    private void generateNumbers(int count) {
        List<Integer> generated = IntStream.range(0, count)
                .map(i -> (int) (Math.random() * 1000) + 1)
                .boxed()
                .collect(Collectors.toList());

        if (generated.stream().noneMatch(n -> n <= 30)) {
            int index = (int) (Math.random() * count);
            generated.set(index, (int) (Math.random() * 30) + 1);
        }

        numbers.clear();
        numbers.addAll(generated);
    }

    /**
     * Renders the number buttons based on numbers list.
     * Clears previous buttons and re-adds them to the panel.
     */
    private void renderButtons() {
        numberPanel.removeAll();
        numberButtons.clear();

        numberPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        for (int i = 0; i < numbers.size(); i++) {
            JButton btn = createNumberButton(i);
            numberButtons.add(btn);

            gbc.gridx = i / 10;
            gbc.gridy = i % 10;
            numberPanel.add(btn, gbc);
        }

        numberPanel.revalidate();
        numberPanel.repaint();
    }

    /**
     * Create a JButton object for specific number
     * Adds an ActionListener that handles:
     * - generating new random numbers if value <=30
     * - showing a warning if value > 30
     *
     * @param i the index of the number in the list
     * @return configured JButton object
     */
    private JButton createNumberButton(int i) {
        int num = numbers.get(i);
        JButton btn = new JButton(String.valueOf(num));
        setNumberButtonProperties(btn);
        btn.addActionListener(e -> {
            if (num <= 30) {
                int extraCount = (int) (Math.random() * 10) + 1;
                for (int j = 0; j < extraCount; j++) {
                    int newNum = (int) (Math.random() * 1000) + 1;
                    numbers.add(newNum);
                }
                renderButtons();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a value smaller or equal to 30.");
            }
        });
        return btn;
    }

    /**
     * Performs quickSort recursively on numbers list.
     *
     * @param low the starting index
     * @param high the ending index
     */
    private void quickSort(int low, int high) {
        if (low < high) {
            int pi = partition(low, high);

            quickSort(low, pi - 1);
            quickSort(pi + 1, high);
        }
    }

    /**
     * Partition list of numbers around a pivot value.
     * Elements smaller (or larger if descending) tha the pivot are moved to the left, others to the right.
     *
     * @param low starting index of partition
     * @param high ending index of partition
     * @return partition index
     */
    private int partition(int low, int high) {
        int pivot = numbers.get(high);
        int partitionIndex = low - 1;

        for (int j = low; j < high; j++) {
            boolean shouldSwap = descending
                    ? numbers.get(j) > pivot
                    : numbers.get(j) < pivot;

            if (shouldSwap) {
                partitionIndex++;
                swap(partitionIndex, j);
                updateUIWithDelay();
            }
        }

        swap(partitionIndex + 1, high);
        updateUIWithDelay();
        return partitionIndex + 1;
    }

    /**
     * Swaps two element in number list.
     *
     * @param i first element index
     * @param j second element index
     */
    private void swap(int i, int j) {
        int temp = numbers.get(i);
        numbers.set(i, numbers.get(j));
        numbers.set(j, temp);
    }

    /**
     * Updates the number buttons on the UI after sorting.
     * Uses a small delay to allow animation effect.
     */
    private void updateUIWithDelay() {
        try {
            SwingUtilities.invokeAndWait(() -> renderButtons());
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Change sort directional
     */
    private void changeSortOrder() {
        descending = !descending;
    }
}
