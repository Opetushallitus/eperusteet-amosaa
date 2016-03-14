angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.dokumentti", {
    url: "/dokumentti",
    ncyBreadcrumb: {
        label: "{{'dokumentti' | kaanna}}"
    },
    resolve: {
    },
    views: {
        "": {
            controller: () => {
            }
        }
    }
}));
