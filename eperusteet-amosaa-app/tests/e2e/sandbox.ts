describe("Sandbox components", () => {
    browser.get("http://localhost:9030/#/fi/sandbox");

    it("should translate objects", () => {
        expect($("div[kaanna=obj]").getText()).toEqual("Hei");
    });

    it("Spinneri näkyy", () => {
        expect($("div[us-spinner]").isDisplayed()).toBeFalsy();
        const spinner = element(by.buttonText("Toggle spinner"));

        spinner.click();
        expect($("div[us-spinner]").isDisplayed()).toBeTruthy();

        spinner.click();
        $("div[us-spinner]").submit
        expect($("div[us-spinner]").isDisplayed()).toBeFalsy();
    });

    it("Editointikontrollit pitäisi toimia globaalilla valitsimella", () => {
        const muokkaa = element.all(by.buttonText("Muokkaa")).get(1);
        expect(muokkaa.isEnabled()).not.toBeFalsy();
        muokkaa.click();
        Helper.allDisabled(by.buttonText("Muokkaa"));

        const tallenna = element(by.buttonText("tallenna"));
        expect(tallenna.isEnabled()).not.toBeFalsy();
        tallenna.click();
        Helper.allEnabled(by.buttonText("Muokkaa"));
    });

    it("Editointikontrollit pitäisi toimia lokaaleilla valitsimella", () => {
        const muokkaa = element.all(by.buttonText("Muokkaa")).get(0);
        muokkaa.click();
        Helper.allDisabled(by.buttonText("Muokkaa"));

        const cancel = element(by.buttonText("Cancel"));
        cancel.click();
        Helper.allEnabled(by.buttonText("Muokkaa"));
    });

    it("Päivämäärän valinnan pitäisi toimia", () => {
        const datepickerButton = $("datepick button");
        const datepickerInput = $("datepick input");
        datepickerButton.click();

        const today = element(by.buttonText("Today"));
        today.click();

        const date = new Date();
        const todaysDate = date.getDay() + "." + (date.getMonth()+1) + "." + date.getFullYear();

        expect(datepickerInput.getAttribute('value')).toEqual( todaysDate );
    });

});
