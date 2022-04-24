# DSBDA
![Hadoop build](https://github.com/meyakovenkoj/DSBDA/actions/workflows/maven.yml/badge.svg)
![Spark build](https://github.com/meyakovenkoj/DSBDA/actions/workflows/spark.yml/badge.svg)

Repository for Data Science and Big Data Analytics
_Course was taken in 2022 in NRNU MEPhI_

## Structure

There will be some homeworks for this course and instructions for building and testing for each of them.

# Homework 1 - Click-map
![Hadoop build](https://github.com/meyakovenkoj/DSBDA/actions/workflows/maven.yml/badge.svg)
### Task

#### Logic

Программа, которая строит тепловую карту кликов по странице
Входные данные: координаты нажатия x,y, userId, timestamp (справочник областей
экрана: &lt;координаты области&gt; - &lt;название области&gt;, справочник температур:
&lt;диапазон значений&gt; - &lt;температура (высокая, средняя, низкая)&gt;)
Выходные данные: название области экрана, количество нажатий (температура)

#### Output format

SequenceFile со Snappy сжатием (плюс команда просмотра содержимого сжатого
файла посредством распаковки). Приложить скриншот просмотра сжатого контента.

#### Addition

Использование Счетчиков. Приложить скриншот использования Счетчиков.

# Homework 2
![Spark build](https://github.com/meyakovenkoj/DSBDA/actions/workflows/spark.yml/badge.svg)
### Task

#### Logic

Программа должна эмулировать 2 типа вычислений - compute intensive (преобладают вычисления) и data intensive (преобладают операции обмена данными). В качестве референсной реализации взять: расчет больших значений факториала для элементов массива BigInteger и группировку элементов массивов по ключам каждый с каждым с редукцией суммированием (сначала генерируется сразу множество массивов, затем попарно элементы группируются по ключам, для объединенного массива выполняется редукция значений суммированием с последующей записью результата). Программа должна поддерживать возможность запуска на разном количестве данных и выполнять таймирование выполнения.

#### Технология подачи новых данных

Sqoop importer (PostgreSQL to HDFS)

#### Технология хранения

HDFS

#### Технология обработки данных

Spark SQL (DataFrame, DataSet)
