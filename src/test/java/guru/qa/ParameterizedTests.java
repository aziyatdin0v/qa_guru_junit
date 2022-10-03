package guru.qa;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.data.Product;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class ParameterizedTests {

    @ValueSource(strings = {"Iphone", "Mac"})
    @ParameterizedTest(name = "Проверка числа результатов поиска на сайте Apple для запроса {0}")
    void appleSearchCommonTest(String testData) {
        open("https://www.apple.com/ru/");
        $("#ac-gn-link-search").click();
        $("#ac-gn-searchform-input").setValue(testData).pressEnter();

        ElementsCollection results = $$("#featured h2");

        for (SelenideElement element : results) {
            element.shouldHave(text(testData));
        }

        $$("#results h2")
                .shouldHave(CollectionCondition.size(30))
                .first()
                .shouldHave(text(testData));
    }

    @CsvSource(value = {
            "Iphone, iPhone – Apple (RU)",
            "Mac, Mac – Apple (RU)"
    })
    @ParameterizedTest(name = "Проверка числа результатов поиска на сайте Apple для запроса {0}")
    void appleSearchCommonTestDifferentExpectedText(String searchQuery, String expectedText) {
        open("https://www.apple.com/ru/");
        $("#ac-gn-link-search").click();
        $("#ac-gn-searchform-input").setValue(searchQuery).pressEnter();

        ElementsCollection results = $$("#featured h2");

        for (SelenideElement element : results) {
            element.shouldHave(text(searchQuery));
        }

        $$("#results h2")
                .shouldHave(CollectionCondition.size(30))
                .first()
                .shouldHave(text(expectedText));
    }

    static Stream<Arguments> appleSiteButtonsTextDataProvider() {
        return Stream.of(
                Arguments.of(List.of("MacBook Air", "MacBook Pro", "iMac 24”", "Mac Pro", "Mac mini", "Сравнение", "Монитор Pro Display XDR", "Monterey"), Product.Mac),
                Arguments.of(List.of("iPad Pro", "iPad", "iPad mini", "Сравнение", "Apple Pencil", "Клавиатуры", "AirPods", "iPadOS"), Product.iPad),
                Arguments.of(List.of("iPhone 13 Pro", "iPhone 13", "iPhone 12", "Сравнение", "AirPods", "AirTag", "iOS 15"), Product.iPhone)
        );
    }

    @MethodSource("appleSiteButtonsTextDataProvider")
    @ParameterizedTest(name = "Проверка отображения названия кнопок для продукта: {1}")
    void selenideSiteButtonsText(List<String> buttonsTexts, Product product) {
        open("https://www.apple.com/ru/");
        $$(".ac-gn-content a").find(text(product.name())).click();
        $$(".chapternav-items a").filter(visible)
                .shouldHave(CollectionCondition.texts(buttonsTexts));
    }

    @EnumSource(Product.class)
    @ParameterizedTest
    void checkProductTest(Product product) {
        open("https://www.apple.com/ru/");
        $$(".ac-gn-content a").find(text(product.name())).shouldBe(visible);
    }
}
