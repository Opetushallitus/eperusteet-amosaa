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
.run(() => {
    CKEDITOR.disableAutoInline = true;
    const basePath = CKEDITOR.basePath;
    CKEDITOR.basePath = _.first(basePath.split("#")) + "ckeditor/";
})
.constant("editorLayouts", {
    minimal: [
        {name: "clipboard", items: ["Cut", "Copy", "Paste", "-", "Undo", "Redo"]},
        {name: "tools", items: ["About"]}
    ],
    simplified: [
        {name: "clipboard", items: ["Cut", "Copy", "Paste", "-", "Undo", "Redo"]},
        {name: "basicstyles", items: ["Bold", "Italic", "Underline", "Strike", "-", "RemoveFormat"]},
        {name: "paragraph", items: ["NumberedList", "BulletedList"]},
        {name: "tools", items: ["About"]}
    ],
    light: [
        {name: "clipboard", items: ["Cut", "Copy", "Paste", "-", "Undo", "Redo"]},
        {name: "basicstyles", items: ["Bold", "Italic", "Underline", "Strike", "-", "RemoveFormat"]},
        {name: "paragraph", items: ["NumberedList", "BulletedList"]},
        {name: "insert", items: ["Table", "Link", "Termi"]},
        {name: "tools", items: ["About"]}
    ],
    normal: [
        {name: "clipboard", items: ["Cut", "Copy", "Paste", "PasteText", "PasteFromWord", "-", "Undo", "Redo"]},
        {name: "basicstyles", items: ["Bold", "Italic", "Underline", "Strike", "-", "RemoveFormat"]},
        {name: "paragraph", items: ["NumberedList", "BulletedList", "Blockquote"]},
        {name: "insert", items: ["Table", "HorizontalRule", "SpecialChar", "Link", "Termi", "epimage"]},
        {name: "tools", items: ["About"]}
    ]
})

    .controller('TermiPluginController', function ($scope, $stateParams, $timeout) {
        $scope.filtered = [];
        $scope.termit = [];
        $scope.model = {
            chosen: null,
            newTermi: {avain: null, termi: null, selitys: null}
        };
        var callback = angular.noop;
        var setDeferred = null;

        function setChosenValue (value) {
            var found = _.find($scope.termit, function (termi: Kasite) {
                return termi.avain === value;
            });
            $scope.model.chosen = found || null;
        }

        function doSort(items) {
            return _.sortBy(items, (item: Kasite) => {
                return KaannaService.kaanna(item.termi).toLowerCase();
            });
        }

        $scope.init = function () {
            TermistoData.getAll().then((res) => {
                $scope.termit = res;
                $scope.filtered = doSort(res);
                if (setDeferred) {
                    setChosenValue(_.cloneDeep(setDeferred));
                    setDeferred = null;
                }
            });
        };

        $scope.filterTermit = function (value) {
            $scope.filtered = _.filter(doSort($scope.termit), function (item) {
                return Algoritmit.match(value, item.termi);
            });
        };

        // data from angular model to plugin
        $scope.registerListener = function (cb) {
            callback = cb;
        };

        $scope.$watch('model.chosen', function (value) {
            callback(value);
        });

        // data from plugin to angular model
        $scope.setValue = function (value) {
            $scope.$apply(function () {
                if (_.isEmpty($scope.termit)) {
                    setDeferred = value;
                } else {
                    setChosenValue(value);
                }
            });
        };

        $scope.addNew = () => {
            $scope.adding = !$scope.adding;
            if ($scope.adding) {
                $scope.model.newTermi = {avain: null, termi: null, selitys: null}
            }
        };

        $scope.closeMessage = () => $scope.message = null;

        $scope.saveNew = () => {
            TermistoData.add($scope.model.newTermi).then(() => {
                $scope.message = 'termi-plugin-tallennettu';
                NotifikaatioService.onnistui("tallennus-onnistui");
                $timeout(() => {
                    $scope.closeMessage();
                }, 8000);
                $scope.adding = false;
                setDeferred = _.clone($scope.model.newTermi.avain);
                $scope.init();
            });
        };

        $scope.cancelNew = () => $scope.adding = false;
    })


.directive("ckeditor", ($q, $filter, $rootScope, editorLayouts, $timeout) => {
    return {
        priority: 10,
        restrict: "A",
        require: "ngModel",
        scope: {
            editorPlaceholder: "@?",
            editMode: "@?editingEnabled"
        },
        link: (scope: any, element, attrs, ctrl) => {
            let placeholderText = null;
            let editingEnabled = (scope.editMode || "true") === "true";
            let inlineEditor: boolean = true;

            if (attrs.classic === "") {
                inlineEditor = false;
            }

            if (editingEnabled) {
                element.addClass("edit-mode");
            }
            element.attr("contenteditable", "true");

            function getPlaceholder() {
                if (scope.editorPlaceholder) {
                    return $filter("kaanna")(scope.editorPlaceholder);
                } else {
                    return "";
                }
            }

            let editor = CKEDITOR.instances[attrs.id];
            if (editor) {
                return;
            }

            let toolbarLayout;
            if (!_.isEmpty(attrs.layout) && !_.isEmpty(editorLayouts[attrs.layout])) {
                toolbarLayout = editorLayouts[attrs.layout];
            } else {
                if (element.is("div")) {
                    toolbarLayout = editorLayouts.normal;
                } else {
                    toolbarLayout = editorLayouts.minimal;
                }
            }

            let ready = false;
            let deferredcall = null;

            let opts = {
                toolbar: toolbarLayout,
                removePlugins: "resize,elementspath,scayt,wsc,image",
                extraPlugins: "quicktable,epimage,termi",
                disallowedContent: "br; tr td{width,height}",
                extraAllowedContent: "img[!data-uid,src]; abbr[data-viite]",
                disableObjectResizing: true, // doesn"t seem to work with inline editor
                language: "fi",
                entities_latin: false,
                sharedSpaces: {
                    top: "ck-toolbar-top"
                },
                readOnly: !editingEnabled,
                title: false,
                customData: {
                    kaanna: KaannaService.kaanna
                }
            };

            if (inlineEditor) {
                editor = CKEDITOR.inline(element[0], opts);
            } else {
                editor = CKEDITOR.replace(element[0], opts);
            }

            // poistetaan enterin käyttö, jos kyseessä on yhden rivin syöttö
            if (!element.is("div")) {
                editor.on("key", (event) => {
                    if (event.data.keyCode === 13) {
                        event.cancel();
                    }
                });
            }

            scope.$on("$translateChangeSuccess", () => {
                placeholderText = getPlaceholder();
                ctrl.$render();
            });

            function setReadOnly(state) {
                editor.setReadOnly(state);
            }

            scope.$on("enableEditing", () => {
                editingEnabled = true;
                if (ready) {
                    setReadOnly(!editingEnabled);
                } else {
                    deferredcall = _.partial(setReadOnly, !editingEnabled);
                }
                element.addClass("edit-mode");
            });

            scope.$on("disableEditing", () => {
                editingEnabled = false;
                editor.setReadOnly(!editingEnabled);
                element.removeClass("edit-mode");
            });

            scope.$on("$destroy", () => {
                $timeout(() => {
                    if (editor && editor.status !== "destroyed") {
                        editor.destroy(false);
                    }
                });

            });

            editor.on("focus", () => {
                if (editingEnabled) {
                    element.removeClass("has-placeholder");
                    $("#toolbar").show();
                    if (_.isEmpty(ctrl.$viewValue)) {
                        editor.setData("");
                    }
                }
            });

            let UUID = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
            let imgSrcPattern = new RegExp("src=\"[^\"]+/" + UUID + "\"", "g");
            function trim(obj) {
                // Replace all nbsps with normal spaces, remove extra spaces and trim ends.
                if (_.isString(obj)) {
                    obj = obj.replace(/&nbsp;/gi, " ").replace(/ +/g, " ").replace(imgSrcPattern, " ").trim();
                }
                return obj;
            }

            let dataSavedOnNotification = false;
            scope.$on("notifyCKEditor", () => {
                if (editor.checkDirty()) {
                    dataSavedOnNotification = true;
                    editor.getSelection().unlock();
                    let data = element.hasClass("has-placeholder") ? "" : editor.getData();
                    ctrl.$setViewValue(trim(data));
                }
                $("#toolbar").hide();
            });

            function updateModel() {
                if (editor.checkDirty()) {
                    editor.getSelection().unlock();
                    let data = editor.getData();
                    scope.$apply(() => {
                        ctrl.$setViewValue(trim(data));
                        // scope.$broadcast("edited");
                    });
                    if (_.isEmpty(data)) {
                        element.addClass("has-placeholder");
                        editor.setData(placeholderText);
                    }
                }

            }

            editor.on("blur", () => {
                if (dataSavedOnNotification) {
                    dataSavedOnNotification = false;
                    return;
                }
                updateModel();
                $("#toolbar").hide();
            });

            editor.on("loaded", () => {
                editor.filter.disallow("br");
                editor.filter.addTransformations([[
                    {
                        element: "img",
                        right: (el) => {
                            // el.attributes.src = EpImageService.getUrl({id: el.attributes["data-uid"]});
                            delete el.attributes.height;
                            delete el.attributes.width;
                        }
                    }
                ]]);
            });

            editor.on("instanceReady", () => {
                ready = true;
                if (deferredcall) {
                    deferredcall();
                    deferredcall = null;
                }
                $rootScope.$broadcast("ckEditorInstanceReady");
            });

            // model -> view

            ctrl.$render = () => {
                if (editor) {
                    if (angular.isUndefined(ctrl.$viewValue) || (angular.isString(ctrl.$viewValue) && _.isEmpty(ctrl.$viewValue) && placeholderText)) {
                        element.addClass("has-placeholder");
                        editor.setData(placeholderText);
                        editor.resetDirty();
                    } else {
                        element.removeClass("has-placeholder");
                        editor.setData(ctrl.$viewValue);
                    }
                }
            };
            placeholderText = getPlaceholder();
        }
    };
});
