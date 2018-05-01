-- phpMyAdmin SQL Dump
-- version 4.7.8
-- https://www.phpmyadmin.net/
--
-- Φιλοξενητής: localhost
-- Χρόνος δημιουργίας: 30 Απρ 2018 στις 16:16:44
-- Έκδοση διακομιστή: 10.0.34-MariaDB
-- Έκδοση PHP: 7.0.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Βάση δεδομένων: `teamge5_a`
--
CREATE DATABASE IF NOT EXISTS `teamge5_a` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `teamge5_a`;

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `paths`
--
-- Δημιουργία: 25 Απρ 2018 στις 07:25:06
--

DROP TABLE IF EXISTS `paths`;
CREATE TABLE `paths` (
  `uid` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `path_raw_google_gpx` varchar(100) NOT NULL,
  `path_smooth_google_gpx` varchar(100) NOT NULL,
  `path_raw_gps_gpx` varchar(100) NOT NULL,
  `tags` int(11) NOT NULL,
  `meters` int(11) NOT NULL,
  `new_path` tinyint(4) DEFAULT '1',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Εκκαθάριση του πίνακα πριν την εισαγωγή `paths`
--

TRUNCATE TABLE `paths`;
--
-- Άδειασμα δεδομένων του πίνακα `paths`
--

INSERT INTO `paths` (`uid`, `player_id`, `path_raw_google_gpx`, `path_smooth_google_gpx`, `path_raw_gps_gpx`, `tags`, `meters`, `new_path`, `created_at`, `updated_at`) VALUES
(1, 1, 'http://83.212.111.234/teamge5_a/mergeFile/merge_gpx.gpx', 'http://83.212.111.234/teamge5_a/mergeFile/merge_gpx.gpx', 'http://83.212.111.234/teamge5_a/mergeFile/merge_gpx.gpx', 0, 0, 1, '2018-04-28 00:47:18', '2018-04-28 07:47:12');

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `players`
--
-- Δημιουργία: 25 Απρ 2018 στις 07:25:06
--

DROP TABLE IF EXISTS `players`;
CREATE TABLE `players` (
  `uid` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `encrypted_password` varchar(80) NOT NULL,
  `salt` varchar(10) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Εκκαθάριση του πίνακα πριν την εισαγωγή `players`
--

TRUNCATE TABLE `players`;
--
-- Άδειασμα δεδομένων του πίνακα `players`
--

INSERT INTO `players` (`uid`, `name`, `email`, `encrypted_password`, `salt`, `created_at`, `updated_at`) VALUES
(2, 'George ', 'biker_gsxr@yahoo.gr', 'nnevqxwMZtIlM9CKDqeV8zWgXo85ZDc4YWE5Mjg0', '9d78aa9284', '2018-04-28 00:53:13', NULL),
(3, 'Iraklis', 'iraklisgrs@gmail.com', 'x3iZiUe0keGuRgTeLA2o44E29cdiNmZiYTAxZGEz', 'b6fba01da3', '2018-04-28 01:39:41', NULL),
(4, 'GEORGE ', 'apopsi2010@gmail.com', 'AIy8y5LqkQncH/x4hA53kwddpKJkNmZkODQ4ZTBl', 'd6fd848e0e', '2018-04-28 03:06:32', NULL);

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `reviews`
--
-- Δημιουργία: 25 Απρ 2018 στις 07:25:06
--

DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews` (
  `uid` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `path_id` int(11) NOT NULL,
  `rated` int(1) NOT NULL,
  `rated_tags` int(1) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Εκκαθάριση του πίνακα πριν την εισαγωγή `reviews`
--

TRUNCATE TABLE `reviews`;
--
-- Άδειασμα δεδομένων του πίνακα `reviews`
--

INSERT INTO `reviews` (`uid`, `player_id`, `path_id`, `rated`, `rated_tags`, `created_at`, `updated_at`) VALUES
(1, 3, 1, 5, 5, '2018-04-28 07:45:20', NULL);

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `users`
--
-- Δημιουργία: 25 Απρ 2018 στις 07:25:06
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(5) UNSIGNED NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Εκκαθάριση του πίνακα πριν την εισαγωγή `users`
--

TRUNCATE TABLE `users`;
--
-- Άδειασμα δεδομένων του πίνακα `users`
--

INSERT INTO `users` (`id`, `username`, `password`) VALUES
(1, 'ira', '09de0687868c9eb940f26829eaabda624eb6493a'),
(2, 'george', '912ea04866a15a029b44ffa01c27891fb1dfa006'),
(3, 'john', '0d119d994b0400b254e1fb7430119e278de8112f');

--
-- Ευρετήρια για άχρηστους πίνακες
--

--
-- Ευρετήρια για πίνακα `paths`
--
ALTER TABLE `paths`
  ADD PRIMARY KEY (`uid`);

--
-- Ευρετήρια για πίνακα `players`
--
ALTER TABLE `players`
  ADD PRIMARY KEY (`uid`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Ευρετήρια για πίνακα `reviews`
--
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`uid`);

--
-- Ευρετήρια για πίνακα `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT για άχρηστους πίνακες
--

--
-- AUTO_INCREMENT για πίνακα `paths`
--
ALTER TABLE `paths`
  MODIFY `uid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT για πίνακα `players`
--
ALTER TABLE `players`
  MODIFY `uid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT για πίνακα `reviews`
--
ALTER TABLE `reviews`
  MODIFY `uid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT για πίνακα `users`
--
ALTER TABLE `users`
  MODIFY `id` int(5) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
