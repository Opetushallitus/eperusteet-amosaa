angular
    .module("app")
    .filter("unsafe", $sce => val => $sce.trustAsHtml(val))
    .filter("stripTags", () => val => String(val || "").replace(/<[^>]+>/gm, ""))
    .filter("aikaleima", $filter => {
        const mapping = {
            date: "d.M.yyyy",
            default: "d.M.yyyy H:mm:ss",
            short: "d.M.yyyy H:mm",
            time: "H:mm"
        };

        return (input, format, defaultKey) => {
            if (!input) {
                return defaultKey ? KaannaService.kaanna(defaultKey) : "";
            } else {
                return $filter("date")(input, mapping[format] || mapping.default);
            }
        };
    })
    .filter("arrayFilterByField", () => (value, selectedArray, field) =>
        _.reject(value, object => _.some(selectedArray, selected => object[field] === selected[field]))
    )
    .filter("nimikirjaimet", () => {
        return nimi => {
            return _.reduce(
                (nimi || "").split(" "),
                (memo, osa) => {
                    return memo + (osa ? osa[0] : "");
                },
                ""
            ).toUpperCase();
        };
    })
    .filter("murupolku", () => {
        const MAX_LENGTH = 20;
        return name => {
            if (name.length > MAX_LENGTH) {
                return name.substring(0, MAX_LENGTH) + "...";
            }

            return name;
        };
    })
    .filter("formatArray", () => {
        return (list, name, separator = ", ") => {

            const elements = _.map(list, el => {
                return el[name];
            });

            return elements.join(separator)
        }
    });
