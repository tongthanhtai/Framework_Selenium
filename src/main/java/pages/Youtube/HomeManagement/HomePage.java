package pages.Youtube.HomeManagement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.BasePage;

import java.io.IOException;
import java.util.*;

public class HomePage extends BasePage {

    public HomePage(WebDriver driver) {
        super(driver);
    }

    //LOCATORS
    By LOGIN_BUTTON = By.xpath("//div[contains(text(),'ĐĂNG NHẬP')]//parent::button");
    By USERNAME_TEXTBOX = By.xpath("//input[@name='username']");
    By PASSWORD_TEXTBOX = By.xpath("//input[@name='password']");
    By AVATAR_PROFILE = By.xpath("//img[@alt='avatar']");
    By PROFILE_OPTIONS = By.xpath("//div[@id='login-user-dropdown']");
    By LOGOUT_PROFILE_OPTION = By.xpath("//a[contains(text(), 'Đăng xuất') and not(@class)]");
    By GAMES = By.xpath("//div[@class='ctn-header']//following-sibling::div[not(contains(@class, 'ctn-filter'))]/div");
    By FOOTER_ABOUT_US_OPTION = By.xpath("//*[contains(@aria-label, 'Giới thiệu')]");
    By FOOTER_PROMOTION_OPTION = By.xpath("//*[contains(@aria-label, 'Khuyến mãi')]");
    By FOOTER_GUIDE_OPTION = By.xpath("//*[contains(@aria-label, 'Hướng dẫn')]");
    By MINI_GAME_ICON = By.xpath("//div[@draggable-state='{}' and contains(@style, 'auto auto;')]");
    By MINI_GAMES = By.xpath("//div[@class='vergo-hover']");
    By DISPLAYED_MINI_GAME = By.xpath("//div[@draggable-state='{}' and contains(@style, 'display: block')]");
    By LOGO_NBET = By.xpath("//div[contains(@class , 'logo')]");

    //Hot Match section
    By NEXT_ARROW_ENABLE = By.xpath("//div[contains(@class, 'hotmatch__slider')]//button[contains(@class, 'slick-arrow slick-next')]");
    By NEXT_ARROW_DISABLE = By.xpath("//div[contains(@class, 'hotmatch__slider')]//button[contains(@class, 'slick-next slick-disabled')]");
    By HOT_MATCHES = By.xpath("//div[@class='hot-matches-content']");
    By MATCHES_INFO = By.xpath("//div[@class='match-info']");
    By MATCHES_INFO_LEFT = By.xpath("./div[@class='match-team-info info__left']/p");
    By MATCHES_INFO_DATE_CENTER = By.xpath("./div[@class='kick-off align-self-center']/strong");
    By MATCHES_INFO_TIME_CENTER = By.xpath("./div[@class='kick-off align-self-center']/p");
    By MATCHES_INFO_RIGHT = By.xpath("./div[@class='match-team-info info__right']/p");
    By LOTTERY_BETTING_SECTION = By.xpath("//div[contains(text(), 'lô đề')]");

    //Top Games
    By TOP_GAMES = By.xpath("//div[@class='top-games']//descendant::div[contains(@class, 'slick-slide') and @data-index]//descendant::p[@class='top-games__title']");
    By TOP_GAMES_NEXT_ARROW_ENABLE = By.xpath("//div[contains(@class, 'top-games__slide')]//button[contains(@class, 'slick-arrow slick-next')]");
    By TOP_GAMES_NEXT_ARROW_DISABLE = By.xpath("//div[contains(@class, 'top-games__slide')]//button[contains(@class, 'slick-next slick-disabled')]");

    //Card Games
    By CARD_GAMES = By.xpath("//div[@class='hot-games__item active'] | (//div[@class='card-games'])[1]/div");

    //Live Casino
    By LIVE_CASINO_GAMES = By.xpath("//div[@class='live_casino_warrap']//descendant::div[@class='section--items-item']");

    //News
    By SCROLL_TO_NEWS_SECTION = By.xpath("//h3[contains(text(), 'Khuyến Mãi')]");
    By NEWS_TYPE_TABS = By.xpath("//div[@class='tab-menu-home']//descendant::div[contains(@class, 'nav-item nav-link')]");
    By NEWS = By.xpath("//div[@class='news-home__item']");

    //Live Chat
    By LIVE_CHAT_IFRAME = By.xpath("//iframe[@id='chat-widget-minimized']");
    By LIVE_CHAT_WINDOW_IFRAME = By.xpath("//iframe[@id='chat-widget']");
    By LIVE_CHAT_BUTTON = By.xpath("//button[@type='button' and contains(@aria-label, 'LiveChat')]");
    By LIVE_CHAT_WINDOW = By.xpath("//div[@dir='ltr']//div[@role='main']");

    By POPUP_EVENT = By.xpath("//div[contains(@class, 'image-modal')]");
    By CLOSE_POPUP_EVENT = By.xpath("(//div[contains(@class, 'close')])[2]");

    //ACTIONS
    //Live Chat
    public void clickLiveChatButton() {
        clickElement(driver, LIVE_CHAT_BUTTON);
    }

    public void waitForLiveChatWindowDisplays() {
        waitForPresenceOfElements(driver, LIVE_CHAT_WINDOW);
    }

    public void switchToLiveChatIframe() {
        switchToIframe(driver, LIVE_CHAT_IFRAME);
    }

    public void switchToLiveChatWindowIframe() {
        switchToIframe(driver, LIVE_CHAT_WINDOW_IFRAME);
    }

    //News
    public void scrollToNewsSection() {
        scrollToElement(SCROLL_TO_NEWS_SECTION);
    }

    public void waitForNewsLoads() {
        waitForPresenceOfElements(driver, NEWS);
    }

    public void closePopUp(){
        if(isDisplayElement(driver, POPUP_EVENT)){
            clickElement(driver, CLOSE_POPUP_EVENT);
        }
    }

    //Live Casino
    public void clickRandomLiveCasino() throws InterruptedException {
        clickRandomElement(LIVE_CASINO_GAMES);
    }

    public void scrollToLiveCasinoSection() {
        scrollToElement(LIVE_CASINO_GAMES);
    }

    //Card Games
    public void clickRandomCardGame() throws InterruptedException {
        clickRandomElement(CARD_GAMES);
    }

    //Top Games
    public String clickRandomTopGame() throws InterruptedException {
        String gameName = randomElement(driver, TOP_GAMES).getText();
        String gameXPath = String.format("//div[contains(@class, 'slick-slide slick-active')]//descendant::p[@class='top-games__title' and text()='%s']", gameName);
        By gameLocator = By.xpath(gameXPath);
        boolean iterate = true;
        while (iterate) {
            if (isDisplayElement(driver, gameLocator)) {
                clickElement(driver, gameLocator);
                iterate = false;
            } else {
                if (!isDisplayElement(driver, TOP_GAMES_NEXT_ARROW_DISABLE)) {
                    clickElement(driver, TOP_GAMES_NEXT_ARROW_ENABLE);
                    Thread.sleep(1000);
                } else {
                    iterate = false;
                }
            }
        }
        return gameName;
    }

    //Hot Match section
    public void scrollToHotMatchSection() {
        scrollToElement(LOTTERY_BETTING_SECTION);
    }

    public int getNumberOfHotMatches() {
        List<WebElement> hotMatches = getElements(driver, HOT_MATCHES);
        return hotMatches.size();
    }

    public boolean isNextArrowDisable() {
        return isDisplayElement(driver, NEXT_ARROW_DISABLE);
    }

    public List<List<String>> getHotMatchesDetails() throws InterruptedException {
        List<List<String>> matchesDetails = new ArrayList<>();
        boolean iterate = true;
        while (iterate) {
            List<WebElement> matchesInfo = driver.findElements(MATCHES_INFO);
            for (WebElement matchInfo: matchesInfo) {
                List<String> temp = new ArrayList<>();
                String tempLeft = matchInfo.findElement(MATCHES_INFO_LEFT).getText();
                String tempDateCenter = matchInfo.findElement(MATCHES_INFO_DATE_CENTER).getText();
                String tempTimeCenter = matchInfo.findElement(MATCHES_INFO_TIME_CENTER).getText();
                String tempRight = matchInfo.findElement(MATCHES_INFO_RIGHT).getText();
                if (!Objects.equals(tempLeft, "") && !Objects.equals(tempDateCenter, "")
                        && !Objects.equals(tempTimeCenter, "")
                        && !Objects.equals(tempRight, "")) {
                    temp.add(tempLeft);
                    temp.add(tempDateCenter);
                    temp.add(tempTimeCenter);
                    temp.add(tempRight);
                    if (!matchesDetails.contains(temp)) {
                        matchesDetails.add(temp);
                    }
                }
            }
            if (!isNextArrowDisable()) {
                clickElement(driver, NEXT_ARROW_ENABLE);
                Thread.sleep(1000);
            } else {
                iterate = false;
            }
        }
        return matchesDetails;
    }

    //Home Page
    public void waitForHomePageLoads() throws InterruptedException {
        Thread.sleep(5000);
        waitForUrlMatch(getPageUrlByKey("homePageUrl"));
        waitForPresenceOfElements(driver, AVATAR_PROFILE);
    }

    public void clickMiniGameIcon() {
        clickElement(driver, MINI_GAME_ICON);
        waitForElementDisappear(driver, MINI_GAME_ICON);
    }

    public void clickRandomMiniGame() {
        waitForPresenceOfElements(driver, MINI_GAMES);
        clickRandomElement(MINI_GAMES);
    }

    public void enterLoginInfoOnHeader(String username, String password) {
        inputText(driver, USERNAME_TEXTBOX, username);
        inputText(driver, PASSWORD_TEXTBOX, password);
    }

    public void clickLoginButtonOnHeader() throws InterruptedException {
        clickElement(driver, LOGIN_BUTTON);
        Thread.sleep(2000);
        clickElement(driver, LOGO_NBET);
    }

    public void clickAvatarOnHeader() {
        clickElement(driver, AVATAR_PROFILE);
    }

    public void waitForProfileOptionsDisplay() {
        waitForPresenceOfElements(driver, PROFILE_OPTIONS);
    }

    public void clickLogoutProfileOption() {
        clickElement(driver, LOGOUT_PROFILE_OPTION);
    }

    public void clickRandomGame() {
        clickRandomElement(GAMES);
    }

    public void clickFooterAboutUsOption() {
        clickElement(driver, FOOTER_ABOUT_US_OPTION);
    }

    public void clickFooterPromotionOption() {
        clickElement(driver, FOOTER_PROMOTION_OPTION);
    }

    public void clickFooterGuideOption() {
        clickElement(driver, FOOTER_GUIDE_OPTION);
    }

    //VERIFICATION
    public boolean isValidUrlAndRequestSuccessful(String url) throws IOException, InterruptedException {
        if (!url.isEmpty() && !url.equals("about:blank")) {
            int statusCode = sendGetRequest(url);
            return statusCode == 200;
        } else {
            return false;
        }
    }

    public boolean verifyMiniGames() {
        return isDisplayElement(driver, DISPLAYED_MINI_GAME);
    }

    //Hot Match
    public boolean verifyHotMatchData() throws InterruptedException {
        int totalHotMatches = getNumberOfHotMatches();
        List<List<String>> hotMatchesDetails = getHotMatchesDetails();
        if (hotMatchesDetails.size()!=totalHotMatches) {
            return false;
        }
        for (List<String> hotMatchDetails: hotMatchesDetails) {
            for (String hotMatchDetail : hotMatchDetails) {
                if (Objects.equals(hotMatchDetail, "")) {
                    return false;
                }
            }
        }
        return true;
    }

    public void waitForVerifyHumanCompleted() {
        waitForPresenceOfElements(driver, LOGIN_BUTTON);
    }

    //Top Game
    public boolean verifyTopGameNavigated(String gameName) throws InterruptedException, IOException {
        if (gameName.equalsIgnoreCase("tài xỉu") || gameName.equalsIgnoreCase("baccarat super 6")) {
            Thread.sleep(1000);
            switchToTab(getWindowHandles(), 1);
            String url = getValidUrlAfterWaiting();
            return isValidUrlAndRequestSuccessful(url);
        }
        Map<String, String> expectedNavigateUrls = Map.of(
                "bắn cá", getPageUrlByKey("fishHunterPageUrl"),
                "slots", getPageUrlByKey("slotsGamePageUrl"),
                "lô đề", getPageUrlByKey("lotteryBettingPageUrl"),
                "quick games", getPageUrlByKey("quickGamePageUrl"),
                "table games", getPageUrlByKey("tablesGamePageUrl"),
                "game bài", getPageUrlByKey("playCardsGamePageUrl"),
                "virtual games", getPageUrlByKey("virtualSportsPageUrl")
        );
        String expectedUrl = expectedNavigateUrls.get(gameName.toLowerCase());
        return verifyCurrentUrl(expectedUrl);
    }

    //Card Games - Live Casino
    public boolean verifyUrlNavigated() throws InterruptedException, IOException {
        Thread.sleep(1000);
        switchToTab(getWindowHandles(), 1);
        String url = getValidUrlAfterWaiting();
        return isValidUrlAndRequestSuccessful(url);
    }

    //News
    public boolean verifyNews() throws IOException, InterruptedException {
        int tabsNumber = getElements(driver, NEWS_TYPE_TABS).size();
        for (int i=0; i<tabsNumber; i++) {
            List<WebElement> tabs = getElements(driver, NEWS_TYPE_TABS);
            tabs.get(i).click();
            int newsNumber = getElements(driver, NEWS).size();
            for (int j=0; j<newsNumber; j++) {
                List<WebElement> newsList = getElements(driver, NEWS);
                newsList.get(j).click();
                setPageLoadTimeout(30);
                if (!isValidUrlAndRequestSuccessful(driver.getCurrentUrl())) {
                    return false;
                }
                driver.navigate().back();
                waitForHomePageLoads();
                tabs = getElements(driver, NEWS_TYPE_TABS);
                tabs.get(i).click();
                waitForNewsLoads();
            }
        }
        return true;
    }

    //Live Chat
    public boolean verifyLiveChatWindow() {
        return isDisplayElement(driver, LIVE_CHAT_WINDOW);
    }
}
