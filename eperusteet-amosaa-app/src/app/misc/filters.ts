angular.module("app")

.filter('unsafe', ($sce) =>
        (val) => $sce.trustAsHtml(val))

.filter('stripTags', () =>
        (val) => String(val || "").replace(/<[^>]+>/gm, ''))

.filter('aikaleima', ($filter) => {
    const mapping = {
        date: 'd.M.yyyy',
        default: 'd.M.yyyy H:mm:ss',
        short: 'd.M.yyyy H:mm'
    };

    return (input, format, defaultKey) => {
        if (!input) {
            return defaultKey ? KaannaService.kaanna(defaultKey) : '';
        }
        else {
            return $filter('date')(input, mapping[format] || mapping.default);
        }
    };
})

.filter('arrayFilterByField', () =>
    (value, selectedArray, field) =>
        _.reject(value, (object) =>
            _.some(selectedArray, (selected) =>
                object[field] === selected[field])));
