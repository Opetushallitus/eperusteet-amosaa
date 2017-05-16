angular.module("app", [
    "ui.router",
    "ui.sortable",
    "ui.event",
    "ui.highlight",
    "ui.select",
    "angularSpinner",
    "restangular",
    "ngResource",
    "pascalprecht.translate",
    "ngAnimate",
    "ngSanitize",
    "ngCookies",
    "ui.bootstrap",
    "angular-loading-bar",
    "angularFileUpload",
    "rzModule"
])

// Route configuration
.config(($stateProvider, $urlRouterProvider, $translateProvider, $urlMatcherFactoryProvider, $sceProvider) => {
    $sceProvider.enabled(true);
    $urlRouterProvider.when("", "/fi");
    $urlRouterProvider.when("/", "/fi");
    // $translateProvider.useLoader();
    $urlMatcherFactoryProvider.strictMode(false); // Trailing slash ignored

    $translateProvider.useLoader('LokalisointiLoader');
    $translateProvider.preferredLanguage("fi");
    $translateProvider.useSanitizeValueStrategy(null);
    moment.locale("fi");
})

// HTTP configs
.config(($httpProvider) => {
    $httpProvider.defaults.headers.common["X-Requested-With"] = "XMLHttpRequest";
    $httpProvider.defaults.xsrfHeaderName = "CSRF";
    $httpProvider.defaults.xsrfCookieName = "CSRF";

    $httpProvider.interceptors.push(["$q", ($q) => ({
        response: (res) => res,
        responseError: (res) => {
            if (res.status === 403) {
                NotifikaatioService.varoitus("ei-oikeutta-suorittaa");
            }
            else if (res.status === 400) {
                if (res && res.data && res.data.syy) {
                    const syy = res.data.syy;
                    if (_.isArray(syy)) {
                        _.each(syy, (item: any) => {
                            if (_.size(item.split(" ")) === 1) {
                                NotifikaatioService.varoitus(KaannaService.kaanna(item));
                            }
                            else {
                                NotifikaatioService.varoitus(KaannaService.kaanna(item.split(" ")[1]));
                            }
                        });
                    }
                    else {
                        NotifikaatioService.varoitus(KaannaService.kaanna(res.data.syy));
                    }
                }
                else {
                    NotifikaatioService.varoitus(KaannaService.kaanna("tuntematon-syy"));
                }
            }
            else if (res.status >= 500) {
                NotifikaatioService.varoitus(KaannaService.kaanna("palvelin-virhetilanne"));
            }
            return $q.reject(res);
        }
    })]);
})

// Generate template or templateUrl automatically for states when not defined
// Defaults to <state path>/view.jade or <ui-view></ui-view> depending on abstract
.config(($stateProvider) => {
    const stateNameToPath = (stateName) => {
        let path = "states/" + stateName.slice(5).replace(/\.index/g, "").replace(/\./g, "/");
        if (_.last(path) === "/") {
            path = path.slice(0, path.length - 1);
        }
        return path;
    };

    // TODO:
    // - Lisää tyhjä oletuskontrolleri jos ei ole ja tila/näkymä ei ole abstrakti
    $stateProvider.decorator("views", (state, parent) => {
        _.merge(state.views, _(state.views)
            .map((view, name) => [name, view])
            .map((pair) => {
                // Add @<state> automatically to named views
                if (!_.isEmpty(pair[0])
                    && pair[0] !== "@default"
                    && _.indexOf(pair[0], "@") === -1) {
                    pair[0] += "@" + state.name;
                }
                return pair;
            })
            .fromPairs()
            .value());

        // Set template or templateUrl automatically if not specified
        _.each(state.views, (view, name) => {
            const idx = _.indexOf(name, "@");
            if (idx !== -1
                    && name !== "@default"
                    && !state.views[name].templateUrl
                    && !state.views[name].template) {
                state.views[name].templateUrl = stateNameToPath(state.name) + "/" + name.slice(0, idx) + ".jade";
            }
        });

        return _(parent(state))
            .map((config, name) => {
                if (!config.template && !config.templateUrl) {
                    if (config.abstract) {
                        config.template = "<ui-view></ui-view>";
                    }
                    else {
                        config.templateUrl = stateNameToPath(state.name) + "/view.jade";
                    }
                }
                return [name, config];
            })
            .zipObject()
            .value();
    });
})

.config(["cfpLoadingBarProvider", (cfpLoadingBarProvider) => {
    cfpLoadingBarProvider.parentSelector = "#loading-bar-container";
    cfpLoadingBarProvider.includeSpinner = true;
    cfpLoadingBarProvider.latencyThreshold = 100;
}])

.config(["usSpinnerConfigProvider", usSpinnerConfigProvider => {
    usSpinnerConfigProvider.setDefaults({ color: "#29d", radius: 30, width: 8, length: 16 });
}])

.run(($state, $rootScope, $log, $urlMatcherFactory, $timeout, cfpLoadingBar) => {
    $rootScope.error = null;
    const onError = (event, toState, toParams, fromState, fromParams, error) => {
        if (!$rootScope.error) {
            $rootScope.error = { event, toState, toParams, fromState, fromParams, error };
            $log.error(error);
            $state.go("root.virhe");
        }
    };

    $rootScope.$on("$stateChangeError", onError);
    $rootScope.$on("$stateNotFound", onError);
    $rootScope.$on("$stateChangeStart", (e, ts, tp, fs, fp) => {
        cfpLoadingBar.start();
        if (fs && ts && ts.name && tp.versio && fp.versio && fp.osaId !== tp.osaId) {
            tp.versio = undefined;
        }
    });
    $rootScope.$on("$stateChangeSuccess", () => {
        cfpLoadingBar.complete();
    });
    $rootScope.$on("$stateChangeError", () => {
        cfpLoadingBar.complete();
    });
    $rootScope.$on("$stateNotFound", () => {
        cfpLoadingBar.complete();
    });
})
.run(($rootScope, $timeout, $log, $urlMatcherFactory, $state) => {
    $rootScope.$on("$stateChangeStart", (event, state, params) => {
        if (EditointikontrollitService.isEditing()) {
            event.preventDefault();
            NotifikaatioService.normaali("ohje-editointi-on-paalla");
        }
    });
    $rootScope.$on("$stateChangeSuccess", (event, state, params) => {
        if (!_.some(["fi", "sv", "en"], (val) => val === params.lang)) {
            $timeout(() => $state.go(state.name, { lang: "fi" }, { reload: true }));
        }
    });
})

.run(($rootScope, $anchorScroll, $location) => {
    $rootScope.gotoAnchor = (loc) => {
        $location.hash(loc);
        $anchorScroll();
    };
})
