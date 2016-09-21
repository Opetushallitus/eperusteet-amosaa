interface PaginationObject {
    per?: number;
    total?: number;
    current?: number;
    pages?: number[];
};

namespace PaginationV2 {
    export const addPagination = (container: any[], matcher: (search: string, item: any) => boolean) => {
        const result = {
            items: [],
            temp: [],
            pagination: <PaginationObject>{},
            updater: (search: string = "") => {
                result.temp = _.filter(container, (item) => matcher(search, item));
                result.pagination = Pagination.paginate(result.temp);
                result.selector();
            },
            selector: (page = 0) => {
                result.pagination.current = page;
                result.items = Pagination.selectItems(result.temp, result.pagination);
            }
        };

        result.updater();
        return result;
    };
}

namespace Pagination {
    export const paginate = (collection: Array<any>, perPage: number = 10, current = 0) => {
        const total = Math.ceil(_.size(collection) / perPage);
        return {
            per: perPage,
            total: total,
            current: current,
            pages: _.range(0, total)
        };
    };

    export const selectItems = (collection: Array<any>, pagination: any) => {
        let begin = pagination.per * pagination.current;
        return _.slice(collection, begin, begin + pagination.per);
    };

    // FIXME: Refactor
    // return { select: ..., update: ..., pagination: ..., filtered: ... }
    const Postfix = "Paginated";
    export const addPagination = (scope: any, matcher: (search: string, item: any) => boolean, con: string, prefix = con) => {
        const name = prefix + Postfix;
        const pagination = "$$" + prefix + Postfix + "Page";
        const temp = "$$" + prefix + Postfix;
        const updater = prefix + Postfix + "Update";
        const selector = prefix + Postfix + "Select";

        scope[updater] = (search: string = "") => {
            scope[temp] = _.filter(scope[con], (ops) => matcher(search, ops));
            scope[pagination] = paginate(scope[temp]);
            scope[selector]();
        };

        scope[selector] = (page = 0) => {
            scope[pagination].current = page;
            scope[name] = Pagination.selectItems(scope[temp], scope[pagination]);
        };

        scope[updater]();
    };
}
