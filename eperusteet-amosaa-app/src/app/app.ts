angular.module("app", [
    "ui.router",
    "restangular",
    "pascalprecht.translate"
])

// Route configuration
.config(($stateProvider, $urlRouterProvider, $translateProvider, $urlMatcherFactoryProvider, $sceProvider) => {
    $sceProvider.enabled(true);
    $urlRouterProvider.when("", "/fi");
    $urlRouterProvider.when("/", "/fi");
    // $translateProvider.useLoader();
    $translateProvider.preferredLanguage("fi");
    $urlMatcherFactoryProvider.strictMode(false); // Trailing slash ignored
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

.run(($rootScope, $log, $urlMatcherFactory) => {
    $rootScope.$on("$stateChangeError", (event, toState, toParams, fromState, fromParams, error) => {
        console.log("fail");
        $log.error(error);
    });
});
