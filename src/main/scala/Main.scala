package scumethy.visualize.fx

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Orientation
import scalafx.geometry.Pos.Center
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, TableColumn, TableView, TextField}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{BorderPane, FlowPane, HBox, VBox}

class RelationMatrix(numOfRows: Int, numOfCols: Int) {
    private var _relationMatrix = Array.tabulate(numOfRows, numOfCols)((x, y) ⇒ 0)

    def getRelationMatrixArray: Array[Array[Int]] = _relationMatrix

    val rows: Int = _relationMatrix.length
    val columns: Int = _relationMatrix(0).length

    def printMatrix(): Unit = {
        for (ri ← _relationMatrix.indices; r = _relationMatrix(ri)) {
            println(r.map(_.toString).mkString(" "))
        }
    }

    def setRelation(r: Int, c: Int) {
        _relationMatrix(r)(c) = 1
    }
}

object RelationMatrix {
    def initMatrix(numOfRows: Int, numOfCols: Int) = new RelationMatrix(numOfRows, numOfCols)
}

object Main extends JFXApp {
    stage = new PrimaryStage {
        title = "Relationship Visualize"

        val root = new BorderPane() {
            id = "back-pane"
            val user_box = new FlowPane(Orientation.Vertical) {
                this.vgap = 20
                val setA = new TextField() {
                    id = "set-A-field"
                }
                val setB = new TextField() {
                    id = "set-B-field"
                }
                val expression = new TextField() {
                    id = "expression-field"
                    text = "x + y"
                }
                val printMatrixButton = new Button() {
                    id = "print-matrix-button"
                    text = "Вывести матрицу"
                }
                printMatrixButton.onMouseClicked = (me: MouseEvent) ⇒ {
                    val relationMatrix = createRelationMatrix(setA, setB, expression)

                    relationMatrix.getRelationMatrixArray.foreach(r ⇒ {
                        val relationRow = new HBox()
                        r.foreach(el ⇒ {
                            relationRow.children.add(
                                new TextField() {
                                    id = "relation-field"
                                    text = el.toString
                                    this.alignment = scalafx.geometry.Pos.Center
                                }
                            )
                        })
                        println(relationRow.children)
                        relMatrixPane.children.add(relationRow)
                    })
                    println(relMatrixPane.children)
                }
                children.addAll(setA, setB, expression, printMatrixButton)
            }
            var relMatrixPane = new FlowPane(Orientation.Vertical) {
                this.setId("relMatrixPane")
            }

            left = user_box
            center = relMatrixPane
        }

        scene = new Scene(root, 1000, 500) {
            stylesheets.add("styles.css")
        }
    }

    def createRelationMatrix(setATF: TextField, setBTF: TextField, expressionTF: TextField): RelationMatrix = {
        val setAVals :: setBVals :: Nil = (setATF :: setBTF :: Nil).map(tf ⇒ {
            extractInputFromTextField(tf)
                .split(" ")
                .map(_.toInt)
        })
        val expression = extractInputFromTextField(expressionTF).split(" ")
        val operation = expression(1)
        val isCompareExp = expression.length == 5
        val compareNumber = if (isCompareExp) expression(4)

        var relMatrix = RelationMatrix.initMatrix(setAVals.length, setBVals.length)
        for (i ← setAVals.indices; a = setAVals(i)) {
            for (j ← setBVals.indices; b = setBVals(j)) {
                val isRelation = operation match {
                    case "+" ⇒ a + b == compareNumber
                    case "-" ⇒ a - b == compareNumber
                    case "/" ⇒ a / b == compareNumber
                    case "*" ⇒ a * b == compareNumber
                    case ">" ⇒ a > b
                    case "<" ⇒ a < b
                    case "!=" ⇒ a != b
                    case ">=" ⇒ a >= b
                    case "<=" ⇒ a <= b
                    case "=" ⇒ a == b
                }
                if (isRelation) relMatrix.setRelation(i, j)
            }
        }
        relMatrix
    }

    def extractInputFromTextField(tf: TextField): String = {
        tf.delegate.asInstanceOf[javafx.scene.control.TextField].getText()
    }
}
