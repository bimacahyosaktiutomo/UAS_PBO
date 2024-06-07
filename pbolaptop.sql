-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 07, 2024 at 11:40 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `pbolaptop`
--

-- --------------------------------------------------------

--
-- Table structure for table `akun`
--

CREATE TABLE `akun` (
  `id` int(11) NOT NULL,
  `username` text NOT NULL,
  `password` text NOT NULL,
  `izin` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `akun`
--

INSERT INTO `akun` (`id`, `username`, `password`, `izin`) VALUES
(1, 'admin', 'admin', 'admin'),
(2, 'a', 'a', 'user'),
(3, 'b', 'b', 'user');

-- --------------------------------------------------------

--
-- Table structure for table `barang`
--

CREATE TABLE `barang` (
  `id` int(11) NOT NULL,
  `nama` text NOT NULL,
  `kategori` text NOT NULL,
  `merek` text NOT NULL,
  `warna` text NOT NULL,
  `harga` int(50) NOT NULL,
  `gambar` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `barang`
--

INSERT INTO `barang` (`id`, `nama`, `kategori`, `merek`, `warna`, `harga`, `gambar`) VALUES
(5, 'Corsair Vengeance', 'RAM', 'Corsair', 'PUTIH', 700000, 'src\\main\\resources\\img\\barang\\Corsair Vengeance_7e77491e-1b7d-4d6d-ae38-29a31b55badc_1.png'),
(6, 'Intel Core i7-12500k', 'CPU', 'Intel', 'NONE', 9000000, 'src\\main\\resources\\img\\barang\\Intel Core i7-12500k_32cf4b8c-964b-4130-a320-4135deff6402_1.jpg'),
(7, 'Corsair Flow', 'CASING', 'Corsair', 'PUTIH', 900000, 'src\\main\\resources\\img\\barang\\Corsair Flow_26112c3c-53db-49fe-80ad-cb552e4b6e36_1.png'),
(8, 'Seasonic Vertex', 'PSU', 'Seasonic', 'PUTIH', 4000000, 'src\\main\\resources\\img\\barang\\Seasonic Vertex_8ce43ce5-cae2-46c5-a209-c4e0dd29f838_1.jpg'),
(9, 'MSI MPG-B550', 'MOTHERBOARD', 'MSI', 'HITAM', 2500000, 'src\\main\\resources\\img\\barang\\MSI MPG-B550_7e83ab8c-b07e-4199-938e-1d0e87bbf4a7_1.png'),
(10, 'MSI Ventus Nvidia RTX 3050', 'GPU', 'MSI', 'PERAK', 6400000, 'src\\main\\resources\\img\\barang\\MSI Ventus Nvidia RTX 3050_55774ea9-de45-4ad4-9fc6-6c5cb3cb88c0_1.png'),
(11, 'NZXT H7 Elite', 'CASING', 'NZXT', 'HITAM', 3000000, 'src\\main\\resources\\img\\barang\\NZXT H7 Elite_ab6485ae-a50a-4dff-820c-3adfc4162ae8_1.jpg');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `akun`
--
ALTER TABLE `akun`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `barang`
--
ALTER TABLE `barang`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `akun`
--
ALTER TABLE `akun`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `barang`
--
ALTER TABLE `barang`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
