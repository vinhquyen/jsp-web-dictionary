-- phpMyAdmin SQL Dump
-- version 3.3.2deb1
-- http://www.phpmyadmin.net
--
-- Servidor: localhost
-- Tiempo de generación: 27-06-2010 a las 23:58:26
-- Versión del servidor: 5.1.41
-- Versión de PHP: 5.3.2-1ubuntu4.2

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de datos: `dict`
--
-- CREATE DATABASE `dict` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;
USE `pirineo`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `multilang`
--

DROP TABLE IF EXISTS `multilang`;
CREATE TABLE IF NOT EXISTS `multilang` (
  `id` int(11) NOT NULL,
  `n_def` int(11) NOT NULL COMMENT 'Number of word definition',
  `be` varchar(200) COLLATE utf8_bin NOT NULL,
  `es` varchar(200) COLLATE utf8_bin NOT NULL,
  `ar` varchar(200) COLLATE utf8_bin NOT NULL,
  `ca` varchar(200) COLLATE utf8_bin NOT NULL,
  `fr` varchar(200) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`,`n_def`),
  KEY `id` (`id`),
  KEY `n_def` (`n_def`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Multilingual[benasqués, es, ar, ca, fr]';

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `multilang_ar`
--

DROP TABLE IF EXISTS `multilang_ar`;
CREATE TABLE IF NOT EXISTS `multilang_ar` (
  `id` int(11) NOT NULL,
  `n_def` int(11) NOT NULL COMMENT 'Number of word definition',
  `term` varchar(200) COLLATE utf8_bin NOT NULL,
  KEY `id` (`id`),
  KEY `n_def` (`n_def`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='multilang-aragonés';

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `multilang_be`
--

DROP TABLE IF EXISTS `multilang_be`;
CREATE TABLE IF NOT EXISTS `multilang_be` (
  `id` int(11) NOT NULL,
  `n_def` int(11) NOT NULL COMMENT 'Number of word definition',
  `term` varchar(200) COLLATE utf8_bin NOT NULL,
  KEY `id` (`id`),
  KEY `n_def` (`n_def`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='multilang-benasqués';

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `multilang_ca`
--

DROP TABLE IF EXISTS `multilang_ca`;
CREATE TABLE IF NOT EXISTS `multilang_ca` (
  `id` int(11) NOT NULL,
  `n_def` int(11) NOT NULL COMMENT 'Number of word definition',
  `term` varchar(200) COLLATE utf8_bin NOT NULL,
  KEY `id` (`id`),
  KEY `n_def` (`n_def`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='multilang-catalán';

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `multilang_es`
--

DROP TABLE IF EXISTS `multilang_es`;
CREATE TABLE IF NOT EXISTS `multilang_es` (
  `id` int(11) NOT NULL,
  `n_def` int(11) NOT NULL COMMENT 'Number of word definition',
  `term` varchar(200) COLLATE utf8_bin NOT NULL,
  KEY `id` (`id`),
  KEY `n_def` (`n_def`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='multilang-español';

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `multilang_fr`
--

DROP TABLE IF EXISTS `multilang_fr`;
CREATE TABLE IF NOT EXISTS `multilang_fr` (
  `id` int(11) NOT NULL,
  `n_def` int(11) NOT NULL COMMENT 'Number of word definition',
  `term` varchar(200) COLLATE utf8_bin NOT NULL,
  KEY `id` (`id`),
  KEY `n_def` (`n_def`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='multilang-francés';

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `username` varchar(16) COLLATE utf8_bin NOT NULL DEFAULT '',
  `hash` varbinary(65) DEFAULT NULL COMMENT 'The password stored in SHA-256',
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Estructura Stand-in para la vista `v_word`
--
DROP VIEW IF EXISTS `v_word`;
CREATE TABLE IF NOT EXISTS `v_word` (
`id` int(11)
,`n_def` int(11)
,`term` varchar(64)
,`morf` varchar(63)
,`definition` varchar(1500)
,`validated` tinyint(1)
,`relation` varchar(511)
);
-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `word`
--

DROP TABLE IF EXISTS `word`;
CREATE TABLE IF NOT EXISTS `word` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `term` varchar(64) COLLATE utf8_bin NOT NULL COMMENT 'Término que define la palabra',
  `morf` varchar(63) COLLATE utf8_bin NOT NULL COMMENT 'Word morfology',
  `validated` tinyint(1) DEFAULT '0' COMMENT 'Entry validated?',
  `relation` varchar(511) COLLATE utf8_bin DEFAULT NULL COMMENT 'Other words relationated',
  PRIMARY KEY (`id`),
  UNIQUE KEY `term+morf` (`term`,`morf`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Palabras del diccionario' AUTO_INCREMENT=7155 ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `word_definition`
--

DROP TABLE IF EXISTS `word_definition`;
CREATE TABLE IF NOT EXISTS `word_definition` (
  `id` int(11) NOT NULL COMMENT 'Word identifier',
  `n_def` int(11) NOT NULL DEFAULT '0' COMMENT 'Number of word definition',
  `definition` varchar(1500) COLLATE utf8_bin NOT NULL COMMENT 'Word definition',
  UNIQUE KEY `id+n_def` (`id`,`n_def`),
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Definitions of the words (allows polisemic terms)';

-- --------------------------------------------------------

--
-- Contenido tabla 'user'
--
-- NO EXISTE USUARIO POR DEFECTO
-- Volcar la base de datos para la tabla `user`
--
--INSERT INTO `user` (`username`, `hash`) VALUES
--('admin947', HEX('HASH'));
