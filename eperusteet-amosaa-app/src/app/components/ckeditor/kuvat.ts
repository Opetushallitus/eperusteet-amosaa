/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */

angular.module("app")
.controller("EpImagePluginController", function ($scope, Api, $timeout, $stateParams, FileUploader, $q) {
    $scope.filtered = [];
    $scope.images = [];
    $scope.showPreview = true;
    $scope.model = {
        files: [],
        rejected: [],
        chosen: null
    };
    $scope.scaleError = false;
    $scope.liitteet = Api.one("koulutustoimijat", $stateParams.ktId);
    let setDeferred = null;
    let callback = angular.noop;

    // Uploader kuvan lisäämistä varten
    $scope.uploader = new FileUploader({
        url: $scope.liitteet.getRestangularUrl() + "/kuvat",
        queueLimit: '1'
    });

    // Lisätään esikatselussa olevaan kuvaan nimi, leveys ja korkeus
    $scope.uploader.onAfterAddingFile = (item) => {
        let reader = new FileReader();
        reader.onload = () => {
            let img = new Image();
            img.src = reader.result;

            // Tiedot backendiä varten
            item.formData.push({
                nimi: item.file.name,
                width: img.width,
                height: img.height,
            });
        };
        
        reader.readAsDataURL(item._file);
    };

    // Lisätään uploaderiin kuva suodatin
    $scope.uploader.filters.push({
        name: 'imageFilter',
        fn: (item) => {
            const type = '|' + item.type.slice(item.type.lastIndexOf('/') + 1) + '|';
            return '|jpg|jpeg|png|'.indexOf(type) !== -1;
        }
    });

    // Kuva lähetetty palvelimelle onnistuneesti
    $scope.uploader.onSuccessItem = () => {
        $scope.uploader.queue = [];
        getKuvat();
    };

    $scope.clear = () => {
        $scope.model.chosen = null;
    };

    $scope.init = () => {
        return getKuvat();
    };

    const getMeta = (url, obj) => {
        let img = new Image();
        img.onload = function(){
            obj.width = this.width;
            obj.height = this.height;
            obj.originalWidth = this.width;
            obj.originalHeight = this.height;
            obj.src = url;
        };
        img.src = url;
    };

    const getKuvat = () => $q((resolve, reject) => $scope.liitteet.getList("kuvat")
        .then((images) => {
            _.each(images, (img) => {
                getMeta($scope.liitteet.getRestangularUrl() + "/kuvat/" + img.id, img);
            });
            $scope.images = images;
            $scope.filtered = Algoritmit.doSortByNimi(images);

            resolve();
        }));

    const setChosenValue = (element) => {
        // Etsitään muokattava kuva kaikista kuvista
        const found = _.find($scope.images, (image: any) => {
            return image.id === element.getAttribute("data-uid");
        });

        // Jos ei löydy, ei aseteta kuvaa valituksi
        if (found == null) {
            return;
        }

        $scope.model.chosen = found;

        // Haetaan muokattavalle kuvalle muut attribuutit
        getMeta($scope.liitteet.getRestangularUrl() + "/kuvat/" + found.id, $scope.model.chosen);

        if (element.hasAttribute("alt")) {
            found.alt = element.getAttribute("alt");
        }

    };

    // Rekisteröidään callback
    $scope.registerListener = (cb) => {
        callback = cb;
    };

    // Jos valittu kuva muuttuu lähetetään ckeditor pluginille tieto
    $scope.$watch('model.chosen', (value) => {
        callback(value);
    });

    // Asetetaan muokattavana oleva kuva valituksi
    $scope.setValue = (element) => {
        getKuvat().then(() => setChosenValue(element));
    };

    $scope.getChosen = () => {
        return $scope.model.chosen;
    };

    $scope.widthChange = (img) => {
        $scope.scaleError = false;
        var tmp = img.width / img.originalWidth;
        img.height = Math.round(tmp * img.originalHeight);
    };

    $scope.heightChange = (img) => {
        $scope.scaleError = false;
        var tmp = img.height / img.originalHeight;
        img.width = Math.round(tmp * img.originalWidth);
    };

    $scope.filterImages = (value) => {
        $scope.filtered = _.filter(Algoritmit.doSortByNimi($scope.images), (item) => {
            return Algoritmit.match(value, item.nimi);
        });
    };


})
.directive('ngThumb', ['$window', function($window) {
    var helper = {
        support: !!($window.FileReader && $window.CanvasRenderingContext2D),
        isFile: function(item) {
            return angular.isObject(item) && item instanceof $window.File;
        },
        isImage: function(file) {
            var type =  '|' + file.type.slice(file.type.lastIndexOf('/') + 1) + '|';
            return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
        }
    };

    return {
        restrict: 'A',
        template: '<canvas/>',
        link: function(scope, element, attributes) {
            if (!helper.support) return;

            var params = scope.$eval(attributes.ngThumb);

            if (!helper.isFile(params.file)) return;
            if (!helper.isImage(params.file)) return;

            var canvas = element.find('canvas');
            var reader = new FileReader();

            reader.onload = onLoadFile;
            reader.readAsDataURL(params.file);

            function onLoadFile(event) {
                var img = new Image();
                img.onload = onLoadImage;
                img.src = event.target.result;
            }

            function onLoadImage() {
                var width = params.width || this.width / this.height * params.height;
                var height = params.height || this.height / this.width * params.width;
                canvas.attr({ width: width, height: height });
                canvas[0].getContext('2d').drawImage(this, 0, 0, width, height);
            }
        }
    };
}])
.filter("kuvalinkit", (Api, $stateParams) => {
    return (text) => {
        let modified = false;
        let tmp = angular.element("<div>" + text + "</div>");
        tmp.find("img[data-uid]").each(function () {
            let el = angular.element(this);
            el.wrap("<figure></figure>");
            el.parent().append("<figcaption>" + el.attr("alt") + "</figcaption>");
            el.parent().wrap("<div style=\"text-align: center;\"></div>");

            let url = Api.one("koulutustoimijat", $stateParams.ktId).getRestangularUrl() + "/kuvat/" + el.attr("data-uid");
            if (el.attr("src") !== url) {
                modified = true;
                el.attr("src", url);
            }
        });

        if (modified) {
            return tmp.html();
        }

        return text;
    };
});
