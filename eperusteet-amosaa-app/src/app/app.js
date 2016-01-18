angular.module("app", [
    "ui.router",
    "restangular",
    "ngAnimate",
    "ui.bootstrap"
])
    .config(function ($stateProvider, $urlRouterProvider, $urlMatcherFactoryProvider, $sceProvider) {
    $sceProvider.enabled(true);
    $urlRouterProvider.when("", "/fi");
    $urlRouterProvider.when("/", "/fi");
    $urlMatcherFactoryProvider.strictMode(false); // Trailing slash ignored
})
    .config(function ($stateProvider) {
    var stateNameToPath = function (stateName) {
        var path = "states/" + stateName.slice(5).replace(/\.index/g, "").replace(/\./g, "/");
        if (_.last(path) === "/") {
            path = path.slice(0, path.length - 1);
        }
        return path;
    };
    // TODO:
    // - Lisää tyhjä oletuskontrolleri jos ei ole ja tila/näkymä ei ole abstrakti
    $stateProvider.decorator("views", function (state, parent) {
        _.merge(state.views, _(state.views)
            .map(function (view, name) { return [name, view]; })
            .map(function (pair) {
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
        _.each(state.views, function (view, name) {
            var idx = _.indexOf(name, "@");
            if (idx !== -1
                && name !== "@default"
                && !state.views[name].templateUrl
                && !state.views[name].template) {
                state.views[name].templateUrl = stateNameToPath(state.name) + "/" + name.slice(0, idx) + ".jade";
            }
        });
        return _(parent(state))
            .map(function (config, name) {
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
    .run(function ($rootScope, $log, $urlMatcherFactory) {
    $rootScope.$on("$stateChangeError", function (event, toState, toParams, fromState, fromParams, error) {
        console.log("fail");
        $log.error(error);
    });
});
//# sourceMappingURL=app.js.map