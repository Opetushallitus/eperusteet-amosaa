namespace Termisto {
    export const rakenna = (kasitteet: Array<any>) => {
        return kasitteet;
    };
    export const makeKey = (item) => {
        let termi = _.first(_.compact(_.values(item.termi))) || 'avain';
        return termi.replace(/[^a-zA-Z0-9]/g, '') + (new Date()).getTime();
    }
};