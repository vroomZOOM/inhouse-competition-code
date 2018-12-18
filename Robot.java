/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team7200.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Ultrasonic;
//import edu.wpi.first.wpilibj.DigitalModule;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {

	Command autoCommand;

	SendableChooser<Integer> teamStatus;
	SendableChooser<Integer> autoPlay;

	protected DifferentialDrive m_myRobot;
	protected Joystick driverstick = new Joystick(0);

	protected Compressor c = new Compressor(0);
	Solenoid shootSolenoid = new Solenoid(0);
	Solenoid retractSolenoid = new Solenoid(3);
	Solenoid Deploy = new Solenoid(1);
	Solenoid unDeploy = new Solenoid(2);

	Spark dumperSpark = new Spark(4);
	WPI_TalonSRX talon = new WPI_TalonSRX(15);

	protected int robotEnable;
	protected int drive;// 1 for drive activated through smart dashboard, 0 for not activated

	protected Timer timer;

	protected Gyro gyro = new ADXRS450_Gyro();
	double kp = 0.5;// multiplier for gyro output

	protected String BotName = ("Gravitron");// the name of our bot!

	double stickReverse;// multiply by this when opposite control is needed on demand (button 10 and 12)

	Servo dumpServo = new Servo(6);// create new servo
	Servo sensServo = new Servo(8);
	double servoAngle;// this makes a new servo object

	private static I2C Wire = new I2C(Port.kOnboard, 1);// slave I2C device address 1 (rio is master)

	Ultrasonic sensor = new Ultrasonic(0, 1);// ping, then echo
	int servAng = 30;
	int currentAng;

	@Override
	public void robotInit() {

		teamStatus = new SendableChooser<Integer>();
		teamStatus.addDefault("Team B is the best!", 0);// create new option on smart dashboard
		teamStatus.addObject("TEAM A IS DA BEST", 1);// oh yes it is - creates a new option on smart dashboard
		SmartDashboard.putData("Is Team A the best?", teamStatus);// deploy all these options to smartdashboard

		/****************************************************************************************************/

		autoPlay = new SendableChooser<Integer>();
		autoPlay.addDefault("dump in upper bin", 0);// new option
		autoPlay.addObject("push the button", 1);// new option
		SmartDashboard.putData("Autonomous Mode Chooser", autoPlay);// deploy all these options to smartdashboard

		/****************************************************************************************************/

		Spark m_left0 = new Spark(0); // motors are plugged into ports 0,1,2,3 into the roborio
		Spark m_left1 = new Spark(1);
		SpeedControllerGroup m_left = new SpeedControllerGroup(m_left0, m_left1);// motor 0 and 1 are the left side
																					// motors
		Spark m_right2 = new Spark(2);
		Spark m_right3 = new Spark(3);
		SpeedControllerGroup m_right = new SpeedControllerGroup(m_right2, m_right3);// motor 2 and 3 are the right side
																					// motors
		m_myRobot = new DifferentialDrive(m_left, m_right); // new differential drive - another can be created for
															// another set of wheels

		dumperSpark.set(0);// stop the dumper motor
		talon.set(ControlMode.PercentOutput,0);
		timer = new Timer();
		// timer.start();

		gyro.reset();

		c.setClosedLoopControl(true);// start the compressor

		m_myRobot.arcadeDrive(0, 0);// set drivetrain to 0 movement

		// System.out.println("TEAM A IS AWESOME!!!!");
		sensor.setAutomaticMode(true);

		// messing around because I can
		/****************************************************************************************************/

	}

	/**
	 
	 */
	@Override
	public void autonomousInit() {

		/*
		 * gyro.reset(); timer.start(); // start timer timer.reset();
		 * m_myRobot.arcadeDrive(0, 0);
		 * 
		 * 
		 * 
		 * sensServo.set(servAng); m_myRobot.arcadeDrive(0, 0);
		 * 
		 * double lastdist = sensor.getRangeMM(); System.out.println(lastdist);
		 * 
		 * sensServo.set(servAng++);
		 * 
		 * if (lastdist < sensor.getRangeMM()) {
		 * 
		 * currentAng = servAng + 1;
		 * 
		 * } else { sensServo.set(servAng--); }
		 */
	}

	@Override
	public void autonomousPeriodic() {

		gyro.reset();
		m_myRobot.arcadeDrive(0, 0);
		System.out.println(sensor.getRangeMM());
		double angle = gyro.getAngle();

		sensServo.set(servAng);

		/*
		 * while (sensor.getRangeMM() > 200) { m_myRobot.arcadeDrive(0.5, -angle * kp);
		 * } while (sensor.getRangeMM() <= 200) { m_myRobot.arcadeDrive(0, 0); }
		 * 
		 * 
		 * 
		 * gyro.reset();
		 * 
		 * 
		 * 
		 * m_myRobot.arcadeDrive(0.5, -angle * kp);
		 */
	}

	public void teleopInit() {
		CameraServer.getInstance().startAutomaticCapture();// start the camera
	}

	@Override
	public void teleopPeriodic() {

		double forward = (driverstick.getY() * -1);
		double turn = (driverstick.getX()); // reading the joystick values

		int robotEnable = (int) teamStatus.getSelected();// reading smartdashboard selection

		boolean sendymabob = driverstick.getRawButton(8);
		boolean punch = driverstick.getRawButton(2);// reading buttons
		boolean punch1 = driverstick.getRawButton(4);
		boolean deploy = driverstick.getTrigger();
		boolean suction = driverstick.getRawButton(3);
		talon.set(ControlMode.PercentOutput,0);

		Deploy.set(false);// sets pistons to default position(retract)
		unDeploy.set(true);
		dumperSpark.set(0);
		if (robotEnable == 0) {
			drive = 0;
		} else {
			drive = 1;

		}
		while (driverstick.getPOV() != -1) {

			if (driverstick.getPOV() == 90)

			{
				dumpServo.setAngle(70);// sets servo angle
				m_myRobot.arcadeDrive((driverstick.getY() * -1) * 0.7 * stickReverse, (driverstick.getX()) * 0.7);

			}

			if (driverstick.getPOV() == 180) {

				dumpServo.setAngle(168);
				m_myRobot.arcadeDrive((driverstick.getY() * -1) * 0.7 * stickReverse, (driverstick.getX()) * 0.7);

			}
		}

		if (driverstick.getRawButton(12)) {

			stickReverse = -1.0;
			m_myRobot.arcadeDrive((driverstick.getY() * -1) * 0.7 * stickReverse, (driverstick.getX()) * 0.7);

		}
		if (driverstick.getRawButton(10)) {

			stickReverse = 1;
			m_myRobot.arcadeDrive((driverstick.getY() * -1) * 0.7 * stickReverse, (driverstick.getX()) * 0.7);

		}

		if (deploy == true) {

			Deploy.set(true);
			unDeploy.set(false);

			m_myRobot.arcadeDrive((driverstick.getY() * -1) * 0.7 * stickReverse, (driverstick.getX()) * 0.7);

		}
		if (driverstick.getRawButton(5) == true) {

			talon.set(ControlMode.PercentOutput,1);

			m_myRobot.arcadeDrive((driverstick.getY() * -1) * 0.7 * stickReverse, (driverstick.getX()) * 0.7);
		}

		if (driverstick.getRawButton(6) == true) {

			talon.set(ControlMode.PercentOutput,-1);

			m_myRobot.arcadeDrive((driverstick.getY() * -1) * 0.7 * stickReverse, (driverstick.getX()) * 0.7);
		}
		
		if (suction == true) {
			dumperSpark.set(0.9);
			
			m_myRobot.arcadeDrive((driverstick.getY() * -1) * 0.7 * stickReverse, (driverstick.getX()) * 0.7);
			
		}
		
		shootSolenoid.set(false);// piston inlet closed
		retractSolenoid.set(true);// piston outlet closed

		dumpServo.setAngle(168);// turn off the chute motor

		m_myRobot.arcadeDrive(forward * 0.7 * stickReverse, turn * 0.7);// drive the bot

		// System.out.println(servoAngle);

		/*************************************************************************/

		if (sendymabob == true) {
			Wire.write(1, 1);

		}

		/*************************************************************************/

		if ((punch || punch1) == true)// if button 2 or 4 are pressed, THE FIST deploys
		{
			shootSolenoid.set(true); // open the inlet valve
			retractSolenoid.set(false); // outlet closed

			m_myRobot.arcadeDrive(forward * 0.7 * stickReverse, turn * 0.7 * stickReverse); // drive the bot

			if (driverstick.getRawButton(5) == true) {

				dumperSpark.set(0.9);

				m_myRobot.arcadeDrive(forward * 0.7 * stickReverse, turn * 0.7 * stickReverse);
			}

			if (sendymabob == true) {

				Wire.write(1, 1);// send an I2C byte

			}
		}
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
