package com.example.game.core;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Game {
    private final int FIELD_SIZE;
    private final Map<Coordinate, Integer> squares = new HashMap<>();

    public Game(int fieldSize) {
        FIELD_SIZE = fieldSize;
    }

    /**
     * Возвращает способ обхода поля
     *
     * @param vector направление обхода
     * @return Пару из координат по x и y, по которым обходить поле
     **/
    Pair<List<Integer>, List<Integer>> buildTraversals(@NonNull Vector vector) {
        Pair<List<Integer>, List<Integer>> traversals =
                new Pair<>(new LinkedList<>(), new LinkedList<>());
        for (int i = 0; i < FIELD_SIZE; i++) {
            traversals.first.add(vector.x != 1 ? i : (FIELD_SIZE - i - 1));
            traversals.second.add(vector.y != 1 ? i : (FIELD_SIZE - i - 1));
        }
        return traversals;
    }

    /**
     * Возвращает перемещение клетки до препядствия/конца карты
     *
     * @param square клетка, которая передвигается
     * @param vector вектор перемещения
     * @return Перемещение (если остается на месте, то в перемещении 2 одинаковые координаты)
     **/
    Coordinate.Move findNewPosition(@NonNull Coordinate square, @NonNull Vector vector) {
        Coordinate newSquare = square.copy();
        while ((newSquare = newSquare.move(vector)).checkCorrect(FIELD_SIZE)) {
            Integer targetNum = squares.get(newSquare);
            if (targetNum == null) continue;
            if (targetNum.equals(squares.get(square)))
                return new Coordinate.Move(square, newSquare);
            break;
        }
        return new Coordinate.Move(square, newSquare.moveBack(vector));
    }

    /**
     * Функция для перемещения всех цифр
     *
     * @param direction направление перемещения
     * @return лист с перемещениями цифр
     **/
    public Set<Coordinate.Move> doMove(@NonNull Direction direction) {
        Vector vector = direction.getVector();
        Pair<List<Integer>, List<Integer>> traversals = buildTraversals(vector);
        Set<Coordinate.Move> moves = new HashSet<>();
        for (int y : traversals.second)
            for (int x : traversals.first) {
                Coordinate square = new Coordinate(x, y);
                Integer number = squares.get(square);
                // Проверка, что в клетке фигура существует
                if (number == null) continue;
                Coordinate.Move move = findNewPosition(square, vector);
                // Проверка, что фигура двигается
                if (move.from.equals(move.to)) continue;
                moves.add(move);
                // Смена цифры в клетке, если в новой координате есть цифра
                Integer targetNumber = squares.get(move.to);
                squares.put(move.to, number + (targetNumber == null ? 0 : targetNumber));
                // Удаление фигуры с координаты исходника
                squares.remove(square);
            }
        return moves;
    }

    /**
     * Функция исключительно для тестирования, добавляет новую цифру на поле
     *
     * @param square координаты цифры
     * @param number значение цифры
     **/
    void setSquare(@NonNull Coordinate square, int number) {
        squares.put(square, number);
    }

    /**
     * Функция исключительно для тестирования, возвращает мапу координат
     **/
    Map<Coordinate, Integer> getSquares() {
        return new HashMap<>(squares);
    }
}
