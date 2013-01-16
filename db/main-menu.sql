----------------------------
--Запрос Для главного меню--
----------------------------
/*SELECT
f.year, f.month, mt.title, tmp1.plus, tmp2.minus, (IFNULL(tmp1.plus, 0) - IFNULL(tmp2.minus, 0)) AS diff
FROM Finance AS f
INNER JOIN MonthTitle AS mt ON mt.idMonthTitle = f.month
LEFT JOIN (SELECT month, SUM(price) AS plus 
		    FROM Finance 
		    WHERE type = 1 
		    GROUP BY month) AS tmp1 ON tmp1.month = f.month
LEFT JOIN (SELECT month, SUM(price) AS minus 
		    FROM Finance 
		    WHERE type = 0
		    GROUP BY month) AS tmp2 ON tmp2.month = f.month
GROUP BY f.year, f.month, mt.title, tmp1.plus, tmp2.minus, (IFNULL(tmp1.plus, 0) - IFNULL(tmp2.minus, 0))
ORDER BY f.year, f.month*/

--Версия без полей year и month
/*SELECT
strftime("%Y", f.financeDate) AS fyear, 
strftime("%m", f.financeDate) AS fmonth, 
mt.title, tmp1.plus, tmp2.minus, 
(IFNULL(tmp1.plus, 0) - IFNULL(tmp2.minus, 0)) AS diff
FROM Finance AS f
INNER JOIN MonthTitle AS mt ON mt.idMonthTitle = fmonth
LEFT JOIN (SELECT strftime("%m", financeDate) AS month, SUM(price) AS plus 
		    FROM Finance 
		    WHERE type = 1 
		    GROUP BY month) AS tmp1 ON tmp1.month = fmonth
LEFT JOIN (SELECT strftime('%m', financeDate) AS month, SUM(price) AS minus 
		    FROM Finance 
		    WHERE type = 0
		    GROUP BY month) AS tmp2 ON tmp2.month = fmonth
GROUP BY strftime("%Y", f.year), strftime("%m", f.month), mt.title, tmp1.plus, tmp2.minus, (IFNULL(tmp1.plus, 0) - IFNULL(tmp2.minus, 0))
ORDER BY f.year, f.month
*/

------------------------------
--Запрос для описания месяца--
------------------------------
/*SELECT idFinance, reason, price, quantity, type, financeDate FROM Finance
WHERE year = 2012 AND month = 8*/

-------------------------------
--Подробное описание операций--
-------------------------------
/*SELECT * FROM Finance WHERE idFinance = 2*/

--------------------
--Вставка новой операции--
--------------------
/*INSERT INTO Finance(idFinance, reason, price, quantity, type, financeDate, year, month)
values(7, 'date_check', 225, 2, 0, '2012-08-13', 2012, 08)*/

-----------------------
--Обновление операции--
-----------------------
/*UPDATE Finance 
SET reason = 'updated_check', price = 220, quantity = 2, type = 0, financeDate='2012-08-10' 
WHERE idFinance = 6*/
