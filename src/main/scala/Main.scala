package scumethy.visualize.fx

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Orientation
import scalafx.geometry.Pos.Center
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, Label, TableColumn, TableView, TextField}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{BorderPane, FlowPane, HBox, VBox}
import scalafx.scene.shape.Line

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

        val root = new HBox(spacing = 40) {
            id = "back-pane"
            val user_box = new FlowPane(Orientation.Vertical) {
                this.vgap = 20
                val setA = new TextField() {
                    id = "set-A-field"
                }
                val setB = new TextField() {
                    id = "set-B-field"
                }
                val expressionField = new TextField() {
                    id = "expression-field"
                    text = "x + y"
                }
                val printMatrixButton = new Button() {
                    id = "print-matrix-button"
                    text = "Вывести матрицу"
                }
                printMatrixButton.onMouseClicked = (me: MouseEvent) ⇒ {
                    val setAVals :: setBVals :: Nil = (setA :: setB :: Nil).map(tf ⇒ {
                        extractInputFromTextField(tf)
                            .split(" ")
                            .map(_.toInt)
                    })
                    val expression = extractInputFromTextField(expressionField).split(" ")

                    val relationMatrix = createRelationMatrix(setAVals, setBVals, expression)
                    val relationMatrixArray = relationMatrix.getRelationMatrixArray

                    relationMatrixArray.foreach(r ⇒ {
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

                    // relation graph number columns
                    val setAValsColumn = new FlowPane(Orientation.Vertical) {
                        setAVals.foreach(v ⇒ {
                            children.add(
                                new Label(v.toString)
                            )
                        })
                    }
                    val relationsPane = new BorderPane()
                    val setBValsColumn = new FlowPane(Orientation.Vertical) {
                        setBVals.foreach(v ⇒ {
                            children.add(
                                new Label(v.toString)
                            )
                        })
                    }
                    relGraphBox.children.addAll(setAValsColumn, relationsPane, setBValsColumn)

                    // relation graph lines between numbers
                    val (a_x, b_x) = (-40, 40)
                    var (a_y, b_y) = (14, 14)
                    for (i ← setAVals.indices; a = setAValsColumn.children(i)) {
                        for (j ← setBVals.indices; b = setBValsColumn.children(j)) {
                            if (relationMatrixArray(i)(j) == 1) {
                                relationsPane.children.addOne(Line(a_x, a_y, b_x, b_y))
                            }
                            b_y += 30
                        }
                        b_y = 14
                        a_y += 30
                    }
                }
                children.addAll(setA, setB, expressionField, printMatrixButton)
            }
            val relMatrixPane = new FlowPane(Orientation.Vertical) {
                this.setId("relMatrixPane")
            }
            val relGraphBox = new HBox(spacing = 50) {
                this.setId("relGraphBox")
            }

            children.addAll(user_box, relMatrixPane, relGraphBox)
        }

        scene = new Scene(root, 1000, 500) {
            stylesheets.add("styles.css")
        }
    }

    def createRelationMatrix(setA: Array[Int], setB: Array[Int], expression: Array[String]): RelationMatrix = {
        val operation = expression(1)
        val isCompareExp = expression.length == 5
        val compareNumber = if (isCompareExp) expression(4).toInt

        var relMatrix = RelationMatrix.initMatrix(setA.length, setB.length)
        for (i ← setA.indices; a = setA(i)) {
            for (j ← setB.indices; b = setB(j)) {
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
