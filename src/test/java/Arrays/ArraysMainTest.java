package Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

public class ArraysMainTest {

    @ParameterizedTest
    @MethodSource("arrayParametersProvider")
    void shouldReturnNewArrayPast4(int[] a, int[] b) {
        Assertions.assertEquals(Arrays.toString(a),Arrays.toString(ArraysMain.arraysRemake(b)));
    }

    private static Stream<Arguments> arrayParametersProvider() {
        return Stream.of(
                Arguments.arguments(new int[]{3, 3},new int[]{3, 2, 4, 1, 4, 3, 3}),
                Arguments.arguments(new int[] {8, 9}, new int[]{0, 2, 3, 1, 4, 8, 9}),
                Arguments.arguments(new int[]{1, 0, 3, 3}, new int[]{1, 0, 4, 1, 0, 3, 3}),
                Arguments.arguments(new int[]{7, 7}, new int[]{4, 2, 4, 1, 4, 7, 7})
        );
    }
    @Test
    void shouldReturnRunTimeException(){
        Assertions.assertThrows(RuntimeException.class, () -> ArraysMain.arraysRemake(new int[]{3, 1, 8, 9}));
    }

    @ParameterizedTest
    @MethodSource("booleanParametersProvider")
    void shouldReturnTrueIfThereIs1And4(boolean a, int[] b){
        Assertions.assertEquals(a, ArraysMain.arrayTrueFalse(b));
    }

    private static Stream<Arguments> booleanParametersProvider() {
        return Stream.of(
                Arguments.arguments(true, new int[]{4, 1, 4, 4}),
                Arguments.arguments(false, new int[]{1, 1, 1, 1}),
                Arguments.arguments(true, new int[]{1, 1, 1, 4, 1, 1, 1}),
                Arguments.arguments(false, new int[]{4, 4, 4, 4, 4, 4}),
                Arguments.arguments(false, new int[]{3, 5, 6, 7, 9, 0})
        );
    }
}
