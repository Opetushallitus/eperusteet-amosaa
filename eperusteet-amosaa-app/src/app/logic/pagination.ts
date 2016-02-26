namespace Pagination {
    export const paginate = (collection: Array<any>, perPage: number, current = 0) => {
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
};
