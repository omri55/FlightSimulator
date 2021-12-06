package view;

import java.util.Observable;
import java.util.Observer;

import interpreter.Interpreter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import matrix.Matrix;
import matrix.Position;
import models.AirplaneListenerModel;
import models.ConnectModel;
import models.MatrixModel;
import models.Property;

public class ViewModel extends Observable implements Observer {
	
	MatrixModel matrixModel;
	AirplaneListenerModel airplaneModel;
	ConnectModel connectModel;
	Interpreter interpreterModel;
		
	// Properties
	
	public IntegerProperty airplanePosX;
	public IntegerProperty airplanePosY;
	
	public Property<Matrix> propertyMat;
	public Property<String[]> csv;
	
	public StringProperty fileName;
	
	public Property<Position> startPos;
	public Property<Position> exitPos;
	
	// Controls
	
	public DoubleProperty aileron;
	public DoubleProperty elevator;
	public DoubleProperty throttle;
	public DoubleProperty rudder;
	
	
	public Property<String> ipSolver;
	public Property<String> portSolver;
	
	// Connect button
	
	public Property<String> ipSim;
	public Property<String> portSim;
	public StringProperty shortestPath;
	public BooleanProperty isConnectedToSolver;
	
	public ViewModel(MatrixModel matrixModel ,AirplaneListenerModel airplaneModel, ConnectModel connectModel) {
		this.matrixModel = matrixModel;
		this.airplaneModel = airplaneModel;
		this.connectModel = connectModel;
		airplanePosX = new SimpleIntegerProperty();
		airplanePosY = new SimpleIntegerProperty();
		shortestPath = new SimpleStringProperty();
		fileName = new SimpleStringProperty();
		
		//Controls
		aileron = new SimpleDoubleProperty();
		elevator = new SimpleDoubleProperty();
		rudder = new SimpleDoubleProperty();
		throttle = new SimpleDoubleProperty();
		
		propertyMat = new Property<>();
		csv = new Property<>();
		ipSim = new Property<>();
		portSim = new Property<>();
		ipSolver = new Property<>();
		portSolver = new Property<>();
		startPos = new Property<>();
		exitPos = new Property<>();
		
		isConnectedToSolver = new SimpleBooleanProperty();
	}
	
	// Send values to simulator
	public void sendRudderValues() {
		connectModel.sendCommandToSimulator("set /controls/flight/rudder ",rudder.get());
	}
	public void sendAileronValues() {
		connectModel.sendCommandToSimulator("set /controls/flight/aileron ",aileron.get());
	}
	public void sendThrottleValues() {
		connectModel.sendCommandToSimulator("set /controls/engines/current-engine/throttle ",throttle.get());
	}
	public void sendElevatorValues() {
		connectModel.sendCommandToSimulator("set /controls/flight/elevator ",elevator.get());
	}
	
	public void setExitPosition() {
		matrixModel.setExitPosition(exitPos.get());
	}
	
	public void buildMatrix() {
		matrixModel.buildMatrix(csv.get());
	}
	
	public void connectToSimulator() {
		connectModel.connect(ipSim.get(), Integer.parseInt(portSim.get()));
	}
	
	public void calculatePath() {
		matrixModel.calculatePath();
	}
	public void connectToSolver() {
		matrixModel.connect(ipSolver.get(), Integer.parseInt(portSolver.get()));
	}
	
	public void interpret() {
		interpreterModel = new Interpreter();
		new Thread(()->interpreterModel.interpret(Interpreter.lexer(fileName.get()))).start();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		String data = (String)arg;
		if (o == airplaneModel) {
			this.airplanePosX.set(airplaneModel.getAirplanePosition().col); //live airplane's location
			this.airplanePosY.set(airplaneModel.getAirplanePosition().row);
			matrixModel.setStartPosition(new Position(airplanePosX.get(),airplanePosY.get()));
			setChanged();
			notifyObservers("airplane");
		}
		if (o == matrixModel) {
			if (data.equals("matrix")) {
				propertyMat.set(matrixModel.getMatrix());
				airplaneModel.setStartCooX(matrixModel.getStartCooX());
				airplaneModel.setStartCooY(matrixModel.getStartCooY());
				setChanged();
				notifyObservers("matrix");
			}
			if (data.equals("shortest path")) {
				this.shortestPath.set(matrixModel.getShortestPath());
				this.isConnectedToSolver.set(true);
				setChanged();
				notifyObservers("shortest path");
			}

			if (data.equals("not connected")) {
				this.isConnectedToSolver.set(false);
				setChanged();
				notifyObservers("not connected");
			}
		}
	}


}
