package labs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CalculatorApp extends Application {
  private final CalculatorEngine engine = new CalculatorEngine();
  private final TextField display = new TextField("0");
  private final Label error = new Label();
  private boolean fresh = true;

  @Override
  public void start(Stage stage) {
    display.setEditable(false);
    display.setStyle("-fx-font-size: 26px;");
    display.setPrefWidth(310);
    GridPane keys = new GridPane();
    keys.setHgap(7);
    keys.setVgap(7);
    String[][] rows = {
      {"C", "sqrt", "^", "/"},
      {"7", "8", "9", "*"},
      {"4", "5", "6", "-"},
      {"1", "2", "3", "+"},
      {"±", "0", ",", "="}
    };
    for (int r = 0; r < rows.length; r++)
      for (int c = 0; c < 4; c++) {
        String k = rows[r][c];
        Button b = new Button(k);
        b.setPrefSize(72, 45);
        b.setFocusTraversable(false);
        b.setOnAction(e -> press(k));
        keys.add(b, c, r);
      }
    VBox root = new VBox(10, display, error, keys);
    root.setStyle("-fx-padding: 16;");
    Scene scene = new Scene(root);
    scene.addEventFilter(
        KeyEvent.KEY_TYPED,
        e -> {
          String k = e.getCharacter();
          if (k.equals("\r") || k.equals("\n")) k = "=";
          if (k.equals(".")) k = ",";
          if (k.equals("\b")) {
            backspace();
            e.consume();
            return;
          }
          if (!k.isEmpty() && !Character.isISOControl(k.charAt(0)) || k.equals("=")) {
            press(k);
            e.consume();
          }
        });
    scene.addEventFilter(
        KeyEvent.KEY_PRESSED,
        e -> {
          if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
            press("C");
            e.consume();
          }
        });
    stage.setTitle("Калькулятор");
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }

  private void backspace() {
    if (fresh) return;
    String t = display.getText();
    display.setText(t.length() > 1 ? t.substring(0, t.length() - 1) : "0");
  }

  private void press(String k) {
    try {
      error.setText("");
      if (k.matches("[0-9]")) {
        display.setText(fresh || display.getText().equals("0") ? k : display.getText() + k);
        fresh = false;
        return;
      }
      if (k.equals(",")) {
        if (fresh) {
          display.setText("0,");
          fresh = false;
        } else if (!display.getText().contains(",")) display.appendText(",");
        return;
      }
      if (k.equals("C") || k.equalsIgnoreCase("c")) {
        engine.clear();
        display.setText("0");
        fresh = true;
        return;
      }
      double value = CalculatorEngine.parse(display.getText());
      switch (k) {
        case "±":
          display.setText(CalculatorEngine.format(-value));
          fresh = false;
          break;
        case "sqrt":
          display.setText(CalculatorEngine.format(engine.sqrt(value)));
          fresh = false;
          break;
        case "=":
          display.setText(CalculatorEngine.format(engine.equals(value)));
          fresh = true;
          break;
        case "+":
        case "-":
        case "*":
        case "/":
        case "^":
          display.setText(CalculatorEngine.format(engine.operator(value, k, fresh)));
          fresh = true;
          break;
        default:
          throw new IllegalArgumentException("Введите цифру или операцию");
      }
    } catch (IllegalArgumentException ex) {
      error.setText(ex.getMessage());
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
