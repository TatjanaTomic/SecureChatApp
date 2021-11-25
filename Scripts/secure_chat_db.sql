-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema secure_chat_db
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema secure_chat_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `secure_chat_db` DEFAULT CHARACTER SET utf8 ;
USE `secure_chat_db` ;

-- -----------------------------------------------------
-- Table `secure_chat_db`.`USER`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `secure_chat_db`.`USER` (
  `Id` INT NOT NULL AUTO_INCREMENT,
  `Email` VARCHAR(60) NOT NULL,
  `Username` VARCHAR(20) NOT NULL,
  `FirstName` VARCHAR(25) NOT NULL,
  `LastName` VARCHAR(25) NOT NULL,
  `Password` VARCHAR(128) NOT NULL,
  `Salt` VARCHAR(32) NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE INDEX `Email_UNIQUE` (`Email` ASC) VISIBLE,
  UNIQUE INDEX `Username_UNIQUE` (`Username` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `secure_chat_db`.`MESSAGE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `secure_chat_db`.`MESSAGE` (
  `Id` INT NOT NULL AUTO_INCREMENT,
  `DateTime` TIMESTAMP(2) NOT NULL,
  `Content` VARCHAR(5000) NOT NULL,
  `SenderId` INT NOT NULL,
  `ReceiverId` INT NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `fk_MESSAGE_USER_idx` (`SenderId` ASC) VISIBLE,
  INDEX `fk_MESSAGE_USER1_idx` (`ReceiverId` ASC) VISIBLE,
  CONSTRAINT `fk_MESSAGE_USER`
    FOREIGN KEY (`SenderId`)
    REFERENCES `secure_chat_db`.`USER` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_MESSAGE_USER1`
    FOREIGN KEY (`ReceiverId`)
    REFERENCES `secure_chat_db`.`USER` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

USE `secure_chat_db` ;