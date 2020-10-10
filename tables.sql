
CREATE TABLE `hospital` (
  `id` VARCHAR(50) NOT NULL,
  `name` VARCHAR(45) NULL,
  `district` VARCHAR(45) NULL,
  `location_x` INT NULL,
  `location_y` INT NULL,
  `build_date` DATE NULL,
  PRIMARY KEY (`id`));


CREATE TABLE `doctor` (
  `id` VARCHAR(50) NOT NULL,
  `name` VARCHAR(100) NULL,
  `hospital_id` VARCHAR(100) NULL,
  `is_director` TINYINT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`hospital_id`) REFERENCES `hospital` (`id`) ON DELETE CASCADE);


CREATE TABLE `patient` (
  `id` VARCHAR(50) NOT NULL,
  `first_name` VARCHAR(100) NULL,
  `last_name` VARCHAR(100) NULL,
  `district` VARCHAR(10) NULL,
  `location_x` INT NULL,
  `location_y` INT NULL,
  `severity_level` VARCHAR(50) NULL,
  `gender` VARCHAR(10) NULL,
  `contact` VARCHAR(45) NULL,
  `email` VARCHAR(45) NULL,
  `age` INT NULL,
  `admit_date` DATE NULL,
  `admitted_by` VARCHAR(45) NULL,
  `discharge_date` DATE NULL,
  `discharged_by` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`admitted_by`) REFERENCES `doctor` (`id`),
  FOREIGN KEY (`discharged_by`) REFERENCES `doctor` (`id`));


CREATE TABLE `hospital_bed` (
  `id` INT NOT NULL,
  `hospital_id` VARCHAR(50) NOT NULL,
  `patient_id` VARCHAR(50) NULL,
  PRIMARY KEY (`id`, `hospital_id`),
  FOREIGN KEY (`hospital_id`) REFERENCES `hospital` (`id`),
  FOREIGN KEY (`patient_id`) REFERENCES `patient` (`id`));


CREATE TABLE `patient_queue` (
  `id` INT NOT NULL,
  `patient_id` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`patient_id`) REFERENCES `patient` (`id`) ON DELETE CASCADE);


CREATE TABLE `user` (
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(100) NULL,
  `name` VARCHAR(100) NULL,
  `moh` TINYINT NULL,
  `hospital` TINYINT NULL,
  PRIMARY KEY (`username`));
