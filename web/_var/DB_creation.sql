--
-- DataBase: `dict`
--
-- --------------------------------------------------------

--
-- Table Structure from the table `word`
--

CREATE TABLE IF NOT EXISTS `word` (
  `id` int(11) NOT NULL auto_increment,
  `term` varchar(64) collate utf8_bin NOT NULL COMMENT 'Término que define la palabra',
  `morf` varchar(63) collate utf8_bin NOT NULL COMMENT 'Word morfology',
  `definition` varchar(511) collate utf8_bin NOT NULL COMMENT 'Word definition',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `term+morf` (`term`,`morf`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Dictionay words' AUTO_INCREMENT=1 ;

--
-- Some values to the table `word`
--

INSERT INTO `word` (`id`, `term`, `morf`, `definition`) VALUES
(1, 'a', 'f.', 'First letter of the alphabet'),
(2, 'fredom', 'noun', 'the condition of being free; the power to act or speak or think without externally imposed restraints'),
(3, 'gnu', 'noun', 'large African antelope having a head with horns like an ox and a long tufted tail '),
(4, 'Linux', 'noun', 'an open-source version of the UNIX operating system'),
(5, 'Release', 'verb', 'prepare and issue for public distribution or sale; "publish a magazine or newspaper"');

