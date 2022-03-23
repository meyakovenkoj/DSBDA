# DSBDA

Repository for Data Science and Big Data Analytics
_Course was taken in 2022 in NRNU MEPhI_

## Structure

There will be some homeworks for this course and instructions for building and testing for each of them.

# Homework 1 - Click-map

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
