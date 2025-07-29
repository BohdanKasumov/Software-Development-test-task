package ua.kasumov.bohdan;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        new IntroForm();
    }
}

class IntroForm extends JFrame {
    private static final int INCORRECT_NUMBER =0;
    private static final String INTRO_FORM_TEXT = "Intro";
    private static final String LABEL_TEXT = "How many numbers to display?";
    private static final String ENTER_BUTTON_TEXT = "Enter";
    private static final String NUMBER_EXCEPTION_MESSAGE = "Enter positive number";

    private JPanel contentPane;
    private JTextField numberTextField;
    private JButton introButton;

    public IntroForm() {
        createForm();
        introButton.addActionListener(e -> {
            try {
                int count = Integer.parseInt(numberTextField.getText());
                if (count <= INCORRECT_NUMBER) throw new NumberFormatException();
                new SortForm(count);
                dispose();
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(IntroForm.this, NUMBER_EXCEPTION_MESSAGE);
            }
        });
        setTitle(INTRO_FORM_TEXT);
        setContentPane(contentPane);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    private void createForm() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(30, 30, 30, 30), -1, -1));
        Dimension buttonSize = new Dimension(80, 25);

        JLabel introLabel = new JLabel();
        introLabel.setText(LABEL_TEXT);
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
        introButton.setText(ENTER_BUTTON_TEXT);
        introButton.setBackground(Color.BLUE);
        introButton.setForeground(Color.WHITE);
        contentPane.add(introButton, new GridConstraints(2, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, buttonSize, null, 0,
                false));
    }

}

class SortForm extends JFrame {
    private static final int MAX_ALLOWED_VALUE = 30;
    private static final int MAX_ROWS_NUMBER_PANEL = 10;
    private static final int MAX_RANDOM_NUMBER_VALUE = 1000;
    private static final int DELAY_UPDATE_UI = 200;
    private static final int SORT_FORM_WIDTH = 600;
    private static final int SORT_FROM_HEIGHT = 400;
    private static final Color UPDATE_BUTTON_BACKGROUND = Color.RED;
    private static final Color NUMBER_BUTTON_BACKGROUND = Color.BLUE;
    private static final Color UTIL_BUTTON_BACKGROUND = Color.GREEN.darker();
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final String SORT_UTIL_BUTTON_TEXT = "Sort";
    private static final String RESET_UTIL_BUTTON_TEXT = "Reset";
    private static final String SORT_FORM_TEXT = "Sort";

    private final List<JButton> numberButtons = new ArrayList<>();
    private final List<Integer> numbers = new ArrayList<>();
    private final JPanel numberPanel;
    private boolean descending = true;

    public SortForm(int count) {
        setTitle(SORT_FORM_TEXT);
        setSize(SORT_FORM_WIDTH, SORT_FROM_HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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

        JButton sortButton = new JButton(SORT_UTIL_BUTTON_TEXT);
        setUtilButtonProperties(sortButton);
        sortButton.addActionListener(e -> new Thread(() -> {
            quickSort(0, numbers.size() - 1);
            changeSortOrder();
            renderButtons();
        }).start());

        JButton resetButton = new JButton(RESET_UTIL_BUTTON_TEXT);
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
        utilButton.setBackground(UTIL_BUTTON_BACKGROUND);
        setButtonProperties(utilButton);
    }

    /**
     * Set visual properties to number buttons.
     *
     * @param numberButton the button to apply properties to
     */
    private static void setNumberButtonProperties(JButton numberButton) {
        numberButton.setBackground(NUMBER_BUTTON_BACKGROUND);
        setButtonProperties(numberButton);
    }

    /**
     * Set visual properties to buttons.
     *
     * @param button the button to apply properties to
     */
    private static void setButtonProperties(JButton button) {
        Dimension buttonSize = new Dimension(80, 25);
        button.setOpaque(true);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
    }

    /**
     * Generate a list of random integer value. Must contain at least one values equals or less than 30.
     *
     * @param count - the number of random values to generate.
     */
    private void generateNumbers(int count) {
        Random random = new Random();
        List<Integer> generated = IntStream.range(0, count)
                .map(i -> random.nextInt(MAX_RANDOM_NUMBER_VALUE) + 1)
                .boxed()
                .collect(Collectors.toList());

        if (generated.stream().noneMatch(n -> n <= MAX_ALLOWED_VALUE)) {
            int index = random.nextInt(count);
            generated.set(index, random.nextInt(MAX_ALLOWED_VALUE) + 1);
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
            JButton button = createNumberButton(i);
            numberButtons.add(button);

            gbc.gridx = i / MAX_ROWS_NUMBER_PANEL;
            gbc.gridy = i % MAX_ROWS_NUMBER_PANEL;
            numberPanel.add(button, gbc);
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
        int number = numbers.get(i);
        JButton button = new JButton(String.valueOf(number));
        setNumberButtonProperties(button);
        button.addActionListener(event -> {
            if (number <= MAX_ALLOWED_VALUE) {
                generateNumbers(number);
                renderButtons();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a value smaller or equal to 30.");
            }
        });
        return button;
    }

    /**
     * Performs quickSort recursively on numbers list.
     *
     * @param low  the starting index
     * @param high the ending index
     */
    private void quickSort(int low, int high) {
        if (low < high) {
            int pivotIndex = partition(low, high);

            quickSort(low, pivotIndex - 1);
            quickSort(pivotIndex + 1, high);
        }
    }

    /**
     * Partition list of numbers around a pivot value.
     * Elements smaller (or larger if descending) tha the pivot are moved to the left, others to the right.
     *
     * @param low  starting index of partition
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

        highlightButtons(i, j);
        updateUIWithDelay();
    }

    /**
     * Updates the number buttons on the UI after sorting.
     * Uses a small delay to allow animation effect.
     */
    private void updateUIWithDelay() {
        try {
            SwingUtilities.invokeAndWait(numberPanel::repaint);
            Thread.sleep(DELAY_UPDATE_UI);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Highlight buttons to swap
     *
     * @param i first element index
     * @param j second element index
     */
    private void highlightButtons(int i, int j) {
        renderButtons();

        for (JButton button : numberButtons){
            int value= Integer.parseInt(button.getText());
            if (value == numbers.get(i) || value == numbers.get(j)) {
                button.setBackground(UPDATE_BUTTON_BACKGROUND);
            } else {
                button.setBackground(NUMBER_BUTTON_BACKGROUND);
            }
        }

        numberPanel.revalidate();
        numberPanel.repaint();
    }

    /**
     * Change sort directional
     */
    private void changeSortOrder() {
        descending = !descending;
    }
}
