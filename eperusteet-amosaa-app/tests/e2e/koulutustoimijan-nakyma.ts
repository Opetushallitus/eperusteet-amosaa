describe("Koulutustoimijan näkymä", () => {

    it("Uuden yhteisen pohjan luonti", () => {

        browser.get("http://amosaa:amosaa@localhost:9030/");

        element(by.css("h2 > div > span > a[uib-dropdown-toggle]")).click();
        element(by.linkText("Opetushallitus")).click();
        element.all(by.buttonText("Lisää uusi")).get(1).click();
        element(by.css("input[ng-model=\"pohja.nimi\"]")).sendKeys("pohja");
        element(by.buttonText("Lisää")).click();

        element(by.css("a[ng-click=\"muutaTila()\"]")).click();
        element(by.css("div[ng-if=\"tila === 'valmis'\"]")).click();
        element(by.css("a[ng-click=\"muutaTila()\"]")).click();
        element(by.css("div[ng-if=\"tila === 'julkaistu'\"]")).click();
        element(by.css("a[ui-sref=\"root\"]")).click();

    });

    it("Uuden yhteisen osan luominen", () => {
        element.all(by.buttonText("Lisää uusi")).get(0).click();
        element(by.css("input[ng-model=\"yhteinen.nimi\"]")).sendKeys("pohjan testi");
        element(by.buttonText("Lisää")).click();
        element(by.css("a[ui-sref=\"root\"]")).click();
    });

    it("Jaetun tutkinnon luominen", () => {
        element.all(by.buttonText("Lisää jaettu osa")).get(1).click();
        element(by.css("input[ng-model=\"ops.nimi\"]")).sendKeys("jaettu");
        element(by.buttonText("Tallenna")).click();
        element(by.css("a[ui-sref=\"root\"]")).click();
    });
});
