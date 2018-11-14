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
	Solenoid shootSolenoid = new Solenoid(1);
	Solenoid retractSolenoid = new Solenoid(2);
	
	Spark dumperSpark = new Spark(4);
	
	protected int robotEnable;
	protected int drive;
	
	
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		
		teamStatus = new SendableChooser<Integer>();
		teamStatus.addDefault("Team B is the best!",0);
		teamStatus.addObject("TEAM A IS DA BEST", 1);
		SmartDashboard.putData("Is Team A the best?", teamStatus);//deploy all these options to smartdashboard
		
		/****************************************************************************************************/
		
		autoPlay = new SendableChooser<Integer>();
		autoPlay.addDefault("dump in upper bin",0);
		autoPlay.addObject("push the button", 1);
		SmartDashboard.putData("Autonomous Mode Chooser", autoPlay);//deploy all these options to smartdashboard
		
		/****************************************************************************************************/
		
		Spark m_left0 = new Spark(0); // motors are plugged into ports 0,1,2,3 into the roborio
		Spark m_left1 = new Spark(1);
		SpeedControllerGroup m_left = new SpeedControllerGroup(m_left0,m_left1);//motor 0 and 1 are the left side motors
		Spark m_right2 = new Spark(2);
		Spark m_right3 = new Spark(3);
		SpeedControllerGroup m_right = new SpeedControllerGroup(m_right2, m_right3);//motor 2 and 3 are the right side motors
		m_myRobot = new DifferentialDrive(m_left, m_right);
		
		dumperSpark.set(0);//stop the dumper motor
		
		
		c.setClosedLoopControl(true);//start the compressor

		m_myRobot.arcadeDrive(0,0);//set drivetrain to 0 movement
		
	System.out.println("TEAM A IS AWESOME!!!!");
	
	
	
	//messing around because I can
		/****************************************************************************************************/
	
	}

	/**
	 
	 */
	@Override
	public void autonomousInit() {
		
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		
		//autonomous code here
		
		
	}
public void teleopInit() {
	CameraServer.getInstance().startAutomaticCapture();//start the camera
}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		int robotEnable = (int)teamStatus.getSelected();
		
		if (robotEnable == 0) {
			drive = 0;
		}
		else {
			drive = 1;
			
		}
		
		
		
		boolean punch = driverstick.getRawButton(2);
		boolean punch1 = driverstick.getRawButton(4);//these two lines read button 2 and 4 and assign them to punch 1 and punch
		//for pressing the button
		
		double forward = (driverstick.getY()*-1*drive);
		double turn = (driverstick.getX()*drive); //reading the joystick values
		
		shootSolenoid.set(false);//piston inlet closed
		retractSolenoid.set(true);//piston outlet closed
		
		dumperSpark.set(0);// turn off the chute motor
		
		m_myRobot.arcadeDrive(forward*0.6,turn*0.55);// drive the bot
		
		
		
		
		if((punch || punch1) == true)// if button 2 or 4 are pressed, THE FIST deploys
		{
			shootSolenoid.set(true); //open the inlet valve
			retractSolenoid.set(false); //outlet closed
			m_myRobot.arcadeDrive(forward*0.6,turn*0.55); //drive the bot
			
		}
		
		 if (driverstick.getPOV() != (-1))
		{
			if (driverstick.getPOV() == 0) {//read the hat switch (it measures in degrees)
				(dumperSpark).set(0.2);//set dupmer motor to 0.4 forward
				m_myRobot.arcadeDrive(forward*0.6,turn*0.55); //drive the bot
			}
			if (driverstick.getPOV() == 180) {//read hat switch
				dumperSpark.set(-0.2);//set dumper motor to reverse
				m_myRobot.arcadeDrive(forward*0.6,turn*0.55); //drive the bot
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
